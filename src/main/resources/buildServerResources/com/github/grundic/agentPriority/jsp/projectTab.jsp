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
<jsp:useBean id="configuredPriorities" scope="request"
             type="java.util.Map<jetbrains.buildServer.serverSide.SProject, java.util.List<com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor >>"/>

<div class="section noMargin">
    <h2 class="noBorder">Agent priorities</h2>
    <bs:smallNote>Available ordering of build agents:<c:forEach items="${availablePriorities}" var="priority"
                                                                varStatus="pos"><c:out value="${priority.name}"/><c:if
            test="${not pos.last}">, </c:if></c:forEach>.</bs:smallNote>

    <bs:refreshable containerId="prioritiesTable" pageUrl="${pageUrl}">

        <bs:messages key="priorityAdded"/>
        <bs:messages key="priorityUpdated"/>
        <bs:messages key="priorityRemoved"/>

        <c:if test="${not currentProject.readOnly}">
            <div>
                <forms:addButton onclick="BS.AgentPriorityDialog.showAddDialog()">Add Priority</forms:addButton>
            </div>
        </c:if>

        <c:forEach items="${configuredPriorities}" var="entry">
            <c:set var="project" value="${entry.key}"/>
            <c:set var="projectPriorities" value="${entry.value}"/>
            <c:set var="inherited" value="${project != currentProject}"/>

            <c:if test="${inherited}">
                <br/>
                <p>Agent priorities inherited from <admin:editProjectLink projectId="${project.externalId}"><c:out
                        value="${project.name}"/></admin:editProjectLink>:</p>
            </c:if>

            <l:tableWithHighlighting className="parametersTable" highlightImmediately="true" style="width:40%;">
                <tr>
                    <th style="width: 30%">Priority</th>
                    <th colspan="${inherited ? 1 : 2}">Actions</th>
                </tr>
                <c:forEach items="${projectPriorities}" var="priority">
                    <c:choose>
                        <c:when test="${inherited}">
                            <tr>
                                <td> <c:out value='${priority.agentPriority.name}'/></td>
                                <td></td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:set var="onclick" value="BS.AgentPriorityDialog.showEditDialog('${priority.id}', '${priority.agentPriority.name}', ${currentProject.readOnly})"/>
                            <tr>
                                <td class="highlight" onclick="${onclick}"><c:out value='${priority.agentPriority.name}'/></td>
                                <c:if test="${not inherited}">
                                    <td class="edit highlight" onclick="${onclick}"><a href="#" onclick="${onclick}">${currentProject.readOnly ? 'View' : 'Edit'}</a></td>
                                    <c:if test="${not currentProject.readOnly}">
                                        <td class="edit"><a href="#" onclick="BS.AgentPriority.deletePriority('/admin/teamcity-agent-priority/priorities.html', '${priority.id}', '${currentProject.externalId}')">Delete</a></td>
                                    </c:if>
                                </c:if>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </l:tableWithHighlighting>
        </c:forEach>
    </bs:refreshable>


    <bs:modalDialog formId="AgentPriority" title="Add Priority" saveCommand="BS.AgentPriorityDialog.save()" closeCommand="BS.AgentPriorityDialog.close()" action="/admin/teamcity-agent-priority/priorities.html">
        <table class="runnerFormTable" style="width: 99%;">
            <tr>
                <td>
                    <label>Priority type: </label>
                </td>
                <td>
          <span id="priorityType">
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

        <div id="priorityParams"></div>

        <span class="error" id="error_priorityType"></span>

        <div class="popupSaveButtonsBlock">
            <forms:submit label="Save"/>
            <forms:cancel onclick="BS.AgentPriorityDialog.close()"/>
            <forms:saving id="saveProgress"/>
            <input type="hidden" name="projectId" value="${currentProject.externalId}"/>
            <input type="hidden" name="priorityId" value=""/>
            <input type="hidden" name="priorityType" value=""/>
            <input type="hidden" name="savePriority" value="save"/>
        </div>
    </bs:modalDialog>
</div>