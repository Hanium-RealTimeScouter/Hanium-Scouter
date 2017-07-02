/*
 *  Copyright 2015 the original author or authors. 
 *  @https://github.com/scouter-project/scouter
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

package scouter.agent.proxy;

import scouter.agent.trace.TraceContext;

public interface IHttpTrace {
	String getParameter(Object req, String key);

	void start(TraceContext ctx, Object req, Object res);

	void end(TraceContext ctx, Object req, Object res);

	void rejectText(Object res, String text);

	void rejectUrl(Object res, String url);

	String getHeader(Object req, String key);

	void addAsyncContextListener(Object ac);

	TraceContext getTraceContextFromAsyncContext(Object oAsyncContext);

	void setDispatchTransferMap(Object oAsyncContext, long gxid, long caller, long callee, byte xType);

	void setSelfDispatch(Object oAsyncContext, boolean self);

	boolean isSelfDispatch(Object oAsyncContext);
}
