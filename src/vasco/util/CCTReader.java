/* _________________________________________________________________________
 *
 *             Vasco : A Visual Churn Exploration Tool
 *
 *
 *  This file is part of the Vasco project.
 *
 *  Vasco is distributed at:
 *      http://github.com/GEODES-UdeM/Vasco
 *
 *
 *  Copyright (c) 2012, Universite de Montreal
 *  All rights reserved.
 *
 *  This software is licensed under the following license (Modified BSD
 *  License):
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of the Universite de Montreal nor the names of its
 *      contributors may be used to endorse or promote products derived
 *      from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITE DE
 *  MONTREAL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * _________________________________________________________________________
 */

package vasco.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import vasco.model.CGModel;
import vasco.model.Method;
import elude.graphs.cg.AttributeCollector;
import elude.graphs.cg.CallGraphReader;
import elude.graphs.cg.CallGraphReader.AttributeVisitor;
import elude.graphs.cg.CallGraphReader.CGVisitor;
import elude.graphs.cg.EAttribute;
import elude.graphs.cg.EType;
import elude.graphs.cg.attrs.Allocation;
import elude.graphs.cg.attrs.CapturedBy;
import elude.graphs.cg.impl.BasicCallGraphFactory;

public class CCTReader {
	private Map<String,Method> methods;

	public CCTReader() {
		// empty
	}

	public CGModel readFile(InputStream s) throws IOException {
		CallGraphReader reader = new CallGraphReader(s);
		this.methods = new HashMap<String, Method>();
		return reader.parse(new CallGraphBuilder());
	}

	public CGModel readFile(String fileName) throws IOException {
		CGModel model = readFile(new File(fileName));
		model.setFilename(fileName);
        return model;
	}

	public CGModel readFile(File f) throws IOException {
        CGModel model = readFile(new FileInputStream(f));
        model.setFilename(f.getAbsolutePath());
        return model;
    }

	private Method getByID(String id) {
		return this.methods.get(id);
	}

	private class CallGraphBuilder implements CGVisitor<CGModel> {
		private CGModel cgm;
		private AttributeCollector collector;
		private Method root;

		@Override
		public AttributeVisitor visitCallgraph() {
			this.cgm = new CGModel();
			this.root = registerMethod(Strings.get("CCTReader.rootName")); //$NON-NLS-1$
			this.collector = new AttributeCollector();
			this.collector.populate(BasicCallGraphFactory.instance());

			return null;
		}

		private Method registerMethod(String name) {
			Method m = new Method(name);
			this.cgm.addNode(m);
			return m;
		}

		private Method lookup(String id) {
			Method m = methods.get(id);
			if (m == null) {
				throw new RuntimeException(Strings.get("CCTReader.invalidMethodRef") + ": " + id); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return m;
		}

		private void addEdge(String parent, String child) {
			this.cgm.addEdge(this.lookup(parent), this.lookup(child));
		}


		@Override
		public AttributeVisitor visitNode(String id, String method,
				String context) {
			Method m = this.registerMethod(method);
			methods.put(id, m);
			return this.collector.create(m);
		}

		@Override
		public AttributeVisitor visitRoot(String id) {
			this.cgm.addEdge(this.root, this.lookup(id));
			return null;
		}

		@Override
		public AttributeVisitor visitEdge(String from, int site, String to) {
			this.addEdge(from, to);
			return null;
		}

		@Override
		public CGModel end() {
			// Cleanup
			this.collector = null;

			this.cgm.setRoot(this.root);
			this.root = null;

			this.ensureTree(this.cgm.getRoot());
			this.computeAllocs(this.cgm.getRoot());
			this.computeCaptures(this.cgm.getRoot());
			this.computeAllocatedTypes();
			this.computeTypes(this.cgm.getRoot());
			this.resolveCaptures();

			return this.cgm;
		}

		private void ensureTree(Method m) {
			if (m.getTotalAllocations() != 0) {
				String id = null;
				for (String key: methods.keySet()) {
					if (methods.get(key) == m) {
						id = key;
						break;
					}
				}
				System.out.println(Strings.get("CCTReader.notATree") + ": " + m.getName() + " = " + id); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return;
			}
			m.setTotalAllocations(1);
			for (Method child: m.getChildren()) {
				ensureTree(child);
			}
		}

		private int computeAllocs(Method m) {
			int total = m.getNumberOfAllocations();
			for (Method child: m.getChildren()) {
				total += computeAllocs(child);
			}

			m.setTotalAllocations(total);
			return total;
		}

		private int computeCaptures(Method m) {
			int total = m.getNumberOfCaptures();
			for (Method child: m.getChildren()) {
				total += computeCaptures(child);
			}

			m.setTotalCaptures(total);
			return total;
		}

		private int computeTypes(Method m){
			int total = m.getNumberOfTypes();
			for (Method child: m.getChildren()) {
				total += computeTypes(child);
			}

			m.setTotalTypes(total);
			return total;
		}

		private void resolveCaptures() {
			for (Method m: this.cgm) {
				EAttribute[] allocs = m.getAttributes(Allocation.NAME);
				for (EAttribute alloc: allocs) {
					EAttribute[] captures = alloc.getAttributes(CapturedBy.NAME);
					for (EAttribute capt: captures) {
						CapturedBy captBy = (CapturedBy) capt;
//						int count = ((Allocation)alloc).getCount();
						Method capturingMethod = getByID(captBy.getCapturingNode());
						if(capturingMethod != null){
							m.addCapturingMethod(capturingMethod);
							capturingMethod.addAllocatingMethod(m);
						}
					}
				}
			}
		}

		private void computeAllocatedTypes(){
			for(Method m : this.cgm){
				Map<EType, Integer> types = new HashMap<EType, Integer>();
				EAttribute[] allocs = m.getAttributes(Allocation.NAME);
				for (EAttribute alloc: allocs) {
				    Allocation allocation = (Allocation) alloc;
                    EType type = allocation.getType();

                    if (types.containsKey(type)) {
                        types.put(type, types.get(type) + allocation.getCount());
                    } else {
                        types.put(type, allocation.getCount());
                    }
				}
				m.setAllocatedTypes(types);
			}
		}


	}

}
