/*
 *  Copyright 2015 Scouter Project.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package scouter.agent.asm.jdbc;

import scouter.agent.trace.TraceSQL;
import scouter.org.objectweb.asm.MethodVisitor;
import scouter.org.objectweb.asm.Opcodes;
import scouter.org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * BCI for a constructor of Resultset
 * @author Gun Lee (gunlee01@gmail.com)
 */
public class RsInitMV extends LocalVariablesSorter implements Opcodes {

	private final static String TRACESQL = TraceSQL.class.getName().replace('.', '/');
	private final static String METHOD = "rsInit";
	private final static String SIGNATURE = "(Ljava/lang/Object;)V";

	public RsInitMV(int access, String desc, MethodVisitor mv) {
		super(ASM4, access, desc, mv);
	}

	@Override
	public void visitInsn(int opcode) {
		if ((opcode >= IRETURN && opcode <= RETURN)) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACESQL, METHOD, SIGNATURE, false);
		}
		mv.visitInsn(opcode);
	}
}