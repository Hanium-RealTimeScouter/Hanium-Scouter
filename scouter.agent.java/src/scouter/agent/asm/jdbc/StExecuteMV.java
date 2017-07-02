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

import scouter.agent.asm.util.AsmUtil;
import scouter.agent.trace.TraceSQL;
import scouter.lang.step.SqlXType;
import scouter.org.objectweb.asm.Label;
import scouter.org.objectweb.asm.MethodVisitor;
import scouter.org.objectweb.asm.Opcodes;
import scouter.org.objectweb.asm.Type;
import scouter.org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.HashSet;
import java.util.Set;

public class StExecuteMV extends LocalVariablesSorter implements Opcodes {
	private static Set<String> target = new HashSet<String>();
	static {
		target.add("execute");
		target.add("executeQuery");
		target.add("executeUpdate");
		target.add("executeBatch");
	}

	private final Type returnType;
	private final byte methodType;

	public static boolean isTarget(String name) {
		return target.contains(name);
	}

	private final static String TRACESQL = TraceSQL.class.getName().replace('.', '/');
	private final static String START_METHOD = "start";
	private static final String START_SIGNATURE = "(Ljava/lang/Object;Ljava/lang/String;B)Ljava/lang/Object;";
	private final static String END_METHOD = "end";
	private static final String END_SIGNATURE = "(Ljava/lang/Object;Ljava/lang/Throwable;I)V";

	public StExecuteMV(int access, String desc, MethodVisitor mv, String owner, String name) {
		super(ASM4, access, desc, mv);
		this.returnType = Type.getReturnType(desc);
        this.desc = desc;
		this.methodType = methodType(name);
	}

	public static byte methodType(String name) {
		if("execute".equals(name)){
			return SqlXType.METHOD_EXECUTE;
		}
		if("executeQuery".equals(name)){
			return SqlXType.METHOD_QUERY;
		}
		if("executeUpdate".equals(name)){
			return SqlXType.METHOD_UPDATE;
		}
		return SqlXType.METHOD_KNOWN;
	}

	private Label startFinally = new Label();
	private int statIdx;
    private String desc;

	@Override
	public void visitCode() {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		AsmUtil.PUSH(mv, this.methodType);

		mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACESQL, START_METHOD, START_SIGNATURE, false);

		statIdx = newLocal(Type.getType(Object.class));
		mv.visitVarInsn(Opcodes.ASTORE, statIdx);
		mv.visitLabel(startFinally);
		mv.visitCode();
	}

	@Override
	public void visitInsn(int opcode) {
		if ((opcode >= IRETURN && opcode <= RETURN)) {
            int lvPosReturn;
			switch (returnType.getSort()) {
                case Type.ARRAY:
                    if(returnType.getElementType().getSort() == Type.INT) {

                        lvPosReturn = newLocal(returnType);
                        mv.visitVarInsn(Opcodes.ASTORE, lvPosReturn);
                        mv.visitVarInsn(Opcodes.ALOAD, lvPosReturn);

                        mv.visitVarInsn(Opcodes.ALOAD, statIdx);
                        mv.visitInsn(Opcodes.ACONST_NULL);
                        mv.visitVarInsn(Opcodes.ALOAD, lvPosReturn);
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACESQL, "getIntArraySum", "([I)I", false);

                    } else {
                        mv.visitVarInsn(Opcodes.ALOAD, statIdx);
                        mv.visitInsn(Opcodes.ACONST_NULL);
                        AsmUtil.PUSH(mv, -1);
                    }
                    break;
                case Type.BOOLEAN:
				case Type.INT:
					int i = newLocal(returnType);
					mv.visitVarInsn(Opcodes.ISTORE, i);
					mv.visitVarInsn(Opcodes.ILOAD, i);

					mv.visitVarInsn(Opcodes.ALOAD, statIdx);
					mv.visitInsn(Opcodes.ACONST_NULL);

					mv.visitVarInsn(Opcodes.ILOAD, i);

					if(returnType.getSort() == Type.BOOLEAN){
						mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACESQL, "toInt", "(Z)I", false);
					}
					break;
				default:
					mv.visitVarInsn(Opcodes.ALOAD, statIdx);
					mv.visitInsn(Opcodes.ACONST_NULL);
					AsmUtil.PUSH(mv, -1);
			}
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACESQL, END_METHOD, END_SIGNATURE, false);
		}
		mv.visitInsn(opcode);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		Label endFinally = new Label();
		mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
		mv.visitLabel(endFinally);
		mv.visitInsn(DUP);
		int errIdx = newLocal(Type.getType(Throwable.class));
		mv.visitVarInsn(Opcodes.ASTORE, errIdx);

		mv.visitVarInsn(Opcodes.ALOAD, statIdx);
		mv.visitVarInsn(Opcodes.ALOAD, errIdx);
		AsmUtil.PUSH(mv, -3);

		mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACESQL, END_METHOD, END_SIGNATURE, false);
		mv.visitInsn(ATHROW);
		mv.visitMaxs(maxStack + 8, maxLocals + 2);
	}
}