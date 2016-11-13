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
<%@ include file="/include.jsp" %>
<jsp:useBean id="currentProject" type="jetbrains.buildServer.serverSide.SProject" scope="request"/>
<jsp:useBean id="availablePriorities" scope="request"
             type="java.util.Collection<com.github.grundic.agentPriority.prioritisation.AgentPriority >"/>

<div class="section noMargin">
    <h2 class="noBorder">Agent priorities</h2>
    <bs:smallNote>Available ordering of build agents:<c:forEach items="${availablePriorities}" var="priority"
                                                                varStatus="pos"><c:out value="${priority.name}"/><c:if
            test="${not pos.last}">, </c:if></c:forEach>.</bs:smallNote>

    <bs:refreshable containerId="PrioritiesTable" pageUrl="${pageUrl}">

        <bs:messages key="PriorityAdded"/>
        <bs:messages key="PriorityUpdated"/>
        <bs:messages key="PriorityRemoved"/>

        <c:if test="${not currentProject.readOnly}">
            <div>
                <forms:addButton onclick="BS.AgentPriorityDialog.showAddDialog()">Add Priority</forms:addButton>
            </div>
        </c:if>
    </bs:refreshable>


    <bs:modalDialog formId="AgentPriority" title="Add Priority" saveCommand="BS.AgentPriorityDialog.save()" closeCommand="BS.AgentPriorityDialog.close()" action="${AgentPrioritysUrl}">
        <table class="runnerFormTable" style="width: 99%;">
            <tr>
                <td>
                    <label>Priority type: </label>
                </td>
                <td>
          <span id="PriorityType">
            <forms:select name="typeSelector" enableFilter="true" onchange="BS.AgentPriorityDialog.priorityChanged(this)" className="longField" style="width:28em">
                <option value="">-- Select a priority type --</option>
                <c:forEach items="${availablePriorities}" var="priority">
                    <option value="${priority.type}"><c:out value="${priority.name}"/></option>
                </c:forEach>
            </forms:select>
            <forms:saving id="parametersProgress" className="progressRingInline"/>
          </span>
                    <span id="readOnlyPriorityType"></span>
                </td>
            </tr>
        </table>

        <div id="PriorityParams"></div>

        <span class="error" id="error_priorityType"></span>

        <div class="popupSaveButtonsBlock">
            <forms:submit label="Save"/>
            <forms:cancel onclick="BS.AgentPriorityDialog.close()"/>
            <forms:saving id="saveProgress"/>
            <input type="hidden" name="projectId" value="${currentProject.externalId}"/>
            <input type="hidden" name="priorityType" value=""/>
            <input type="hidden" name="priorityId" value=""/>
            <input type="hidden" name="afterAddUrl" value=""/>
            <input type="hidden" name="savePriority" value="save"/>
        </div>
    </bs:modalDialog>
</div>