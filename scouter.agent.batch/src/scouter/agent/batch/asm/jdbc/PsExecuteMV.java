/*
 *  Copyright 2016 Scouter Project.
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

package scouter.agent.batch.asm.jdbc;

import scouter.agent.batch.trace.TraceSQL;

import scouter.org.objectweb.asm.Label;
import scouter.org.objectweb.asm.MethodVisitor;
import scouter.org.objectweb.asm.Opcodes;
import scouter.org.objectweb.asm.Type;
import scouter.org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.HashSet;
import java.util.Set;


public class PsExecuteMV extends LocalVariablesSorter implements Opcodes {
	private static Set<String> target = new HashSet<String>();
	static {
		target.add("execute");
		target.add("executeQuery");
		target.add("executeUpdate");
		target.add("executeBatch");
	}

	public static boolean isTarget(String name) {
		return target.contains(name);
	}

	private final static String TRACESQL = TraceSQL.class.getName().replace('.', '/');
	private final static String START_METHOD = "start";
	private static final String START_SIGNATURE = "()V";
	private final static String END_METHOD = "end";
	private static final String END_SIGNATURE = "()V";
	private final static String ADD_METHOD = "addRow";
	private static final String ADD_SIGNATURE = "(I)V";
	private final static String ADDS_METHOD = "addRows";
	private static final String ADDS_SIGNATURE = "([I)V";

	public PsExecuteMV(int access, String desc, MethodVisitor mv, String owner,String name) {
		super(ASM4,access, desc, mv);
		this.owner = owner;
		this.returnType = Type.getReturnType(desc);
	}
	
	private Label startFinally = new Label();
	private String owner;
	private final Type returnType;

	@Override
	public void visitCode() {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
	    mv.visitFieldInsn(GETFIELD, owner, TraceSQL.CURRENT_TRACESQL_FIELD, "Lscouter/agent/batch/trace/TraceSQL;");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACESQL, START_METHOD, START_SIGNATURE,false);
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
        			mv.visitVarInsn(ALOAD, 0);
        		    mv.visitFieldInsn(GETFIELD, owner, TraceSQL.CURRENT_TRACESQL_FIELD, "Lscouter/agent/batch/trace/TraceSQL;");
                    mv.visitVarInsn(Opcodes.ALOAD, lvPosReturn);
        			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACESQL, ADDS_METHOD, ADDS_SIGNATURE,false);
                }
                break;
            case Type.INT:
            	lvPosReturn = newLocal(returnType);
                mv.visitVarInsn(Opcodes.ISTORE, lvPosReturn);
                mv.visitVarInsn(Opcodes.ILOAD, lvPosReturn);
    			mv.visitVarInsn(ALOAD, 0);
    		    mv.visitFieldInsn(GETFIELD, owner, TraceSQL.CURRENT_TRACESQL_FIELD, "Lscouter/agent/batch/trace/TraceSQL;");
                mv.visitVarInsn(Opcodes.ILOAD, lvPosReturn);
    			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACESQL, ADD_METHOD, ADD_SIGNATURE,false);
                break;
/*            case Type.BOOLEAN:
            	lvPosReturn = newLocal(returnType);
                mv.visitVarInsn(Opcodes.ISTORE, lvPosReturn);
    			mv.visitVarInsn(ALOAD, 0);
    		    mv.visitFieldInsn(GETFIELD, owner, TraceSQL.CURRENT_TRACESQL_FIELD, "Lscouter/agent/batch/trace/TraceSQL;");
                mv.visitVarInsn(ALOAD, 0);
    			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, STATMENT, GETUPDATECOUNT_METHOD, GETUPDATECOUNT_SIGNATURE,false);
    			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACESQL, ADD_METHOD, ADD_SIGNATURE,false);    		    
                mv.visitVarInsn(Opcodes.ILOAD, lvPosReturn);           	
            	break;*/
              default:
			}
			
			// Return 결과 셋팅
			mv.visitVarInsn(ALOAD, 0);
		    mv.visitFieldInsn(GETFIELD, owner, TraceSQL.CURRENT_TRACESQL_FIELD, "Lscouter/agent/batch/trace/TraceSQL;");
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACESQL, END_METHOD, END_SIGNATURE,false);
		}
		mv.visitInsn(opcode);
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		Label endFinally = new Label();
		mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
		mv.visitLabel(endFinally);
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
	    mv.visitFieldInsn(GETFIELD, owner, TraceSQL.CURRENT_TRACESQL_FIELD, "Lscouter/agent/batch/trace/TraceSQL;");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TRACESQL, END_METHOD, END_SIGNATURE,false);
		
		int errIdx = newLocal(Type.getType(Throwable.class));
		mv.visitVarInsn(Opcodes.ASTORE, errIdx);
		mv.visitInsn(ATHROW);
		
		mv.visitMaxs(maxStack + 8, maxLocals + 2);
	}
	
    public static void main(String[] args) {
        Type type = Type.getReturnType("(Z)[I");
        System.out.println("type = " + type.getSort());
        System.out.println("dim = " + type.getDimensions());
        System.out.println("element = " + type.getElementType());
    }
}