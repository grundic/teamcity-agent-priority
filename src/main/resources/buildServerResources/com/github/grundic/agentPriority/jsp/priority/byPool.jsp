<%--
  ~ The MIT License
  ~
  ~ Copyright (c) 2016 Grigory Chernyshev.
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in
  ~ all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  ~ THE SOFTWARE.
  --%>
<%@ include file="/include-internal.jsp"%>
<jsp:useBean id="propertiesBean" type="com.github.grundic.agentPriority.prioritisation.AgentPriorityBean" scope="request"/>

<tr>
    <td><label for="poolName">Priority pool name:</label><l:star/></td>
    <td>
        <props:textProperty name="poolName" className="longField"/>
        <span class="smallNote">Provide the name of the pool to prioritize.</span>
        <span class="smallNote">Please note: This value is case sensitive.</span>
        <span class="error" id="error_poolName"></span>
    </td>
</tr>