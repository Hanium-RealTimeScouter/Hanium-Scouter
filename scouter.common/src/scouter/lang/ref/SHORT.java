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

package scouter.lang.ref;

public class SHORT {
	public SHORT() {
	}

	public SHORT(short value) {
		this.value = value;
	}

	public short value;

	@Override
	public int hashCode() {
		return (int) value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SHORT) {
			SHORT other = (SHORT) obj;
			return this.value == other.value;
		}
		return false;
	}
}
