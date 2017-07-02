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

package scouter.lang.step;

import java.io.IOException;

import scouter.io.DataInputX;
import scouter.io.DataOutputX;

public class ApiCallSum extends StepSummary {

	public int hash;
	public int count;
	public long elapsed;
	public long cputime;
	public int error;
	public byte opt;

	public byte getStepType() {
		return StepEnum.APICALL_SUM;
	}

	public void write(DataOutputX out) throws IOException {
		out.writeDecimal(hash);
		out.writeDecimal(count);
		out.writeDecimal(elapsed);
		out.writeDecimal(cputime);
		out.writeDecimal(error);
		out.writeByte(opt);
		switch (opt) {
		case 0:
			break;
		default:
			throw new IOException("not allowed opt");
		}

	}

	public Step read(DataInputX in) throws IOException {
		this.hash = (int) in.readDecimal();
		this.count = (int) in.readDecimal();
		this.elapsed = in.readDecimal();
		this.cputime = in.readDecimal();
		this.error = (int) in.readDecimal();
		this.opt = in.readByte();
		
		return this;
	}
}