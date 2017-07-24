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


<p>This priority orders build agents in correspondence with the status of the build:
    successful builds would give higher agent's priority. Here is the formula for making calculations:
    <i>coefficient = index * historyCoefficient + baseScale</i>, where <b>index</b> is sequential build number.</p>

<tr>
    <td><label for="buildLimit">Build limit:</label><l:star/></td>
    <td>
        <props:textProperty name="buildLimit" className="longField"/>
        <span class="smallNote">Number of builds for checking the status. Very high value could possibly slow down build distribution.</span>
        <span class="error" id="error_buildLimit"></span>
    </td>
</tr>

<tr>
    <td><label for="historyCoefficient">History coefficient:</label><l:star/></td>
    <td>
        <props:textProperty name="historyCoefficient" className="longField"/>
        <span class="smallNote">The value is multiplied with the build index number. Usually this should be negative or zero, so older builds gain less weight.</span>
        <span class="error" id="error_historyCoefficient"></span>
    </td>
</tr>

<tr>
    <td><label for="buildLimit">Base scale:</label><l:star/></td>
    <td>
        <props:textProperty name="baseScale" className="longField"/>
        <span class="smallNote">The value is added to calculated history coefficient, multiplied by build index number.</span>
        <span class="error" id="error_buildLimit"></span>
    </td>
</tr>

<tr>
    <td><label for="successfulScore">Successful score:</label><l:star/></td>
    <td>
        <props:textProperty name="successfulScore" className="longField"/>
        <span class="smallNote">Value that should be added to history coefficient for successful builds.</span>
        <span class="error" id="error_successfulScore"></span>
    </td>
</tr>

<tr>
    <td><label for="failedScore">Failed score:</label><l:star/></td>
    <td>
        <props:textProperty name="failedScore" className="longField"/>
        <span class="smallNote">Value that should be added to history coefficient for failed builds.</span>
        <span class="error" id="error_failedScore"></span>
    </td>
</tr>
