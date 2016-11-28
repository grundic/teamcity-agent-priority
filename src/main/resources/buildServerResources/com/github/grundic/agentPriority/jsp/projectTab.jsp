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
             type="java.util.Collection<com.github.grundic.agentPriority.prioritisation.AgentPriority>"/>
<jsp:useBean id="configuredPriorities" scope="request"
             type="java.util.Map<jetbrains.buildServer.serverSide.SProject, java.util.List<com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor>>"/>
<jsp:useBean id="availableBuilds" scope="request"
             type="java.util.List<jetbrains.buildServer.serverSide.SBuildType>"/>

<c:url var="prioritiesUrl" value="/admin/teamcity-agent-priority/priorities.html"/>


<bs:linkScript>
    /js/bs/queueLikeSorter.js
</bs:linkScript>

<div class="section noMargin">
    <h2 class="noBorder">Agent priorities</h2>
    <bs:smallNote>Available ordering of build agents:<c:forEach items="${availablePriorities}" var="priority"
                                                                varStatus="pos"><c:out value="${priority.name}"/><c:if
            test="${not pos.last}">, </c:if></c:forEach>.</bs:smallNote>


    <c:if test="${not currentProject.readOnly}">
        <div style="display:inline-block;">
            <forms:addButton onclick="BS.AgentPriorityDialog.showAddDialog()">Add Priority</forms:addButton>
        </div>
    </c:if>

    <c:forEach items="${configuredPriorities}" var="entry">
        <c:set var="project" value="${entry.key}"/>
        <c:set var="projectPriorities" value="${entry.value}"/>
        <c:set var="inherited" value="${project != currentProject}"/>

        <c:if test="${!inherited && projectPriorities.size() > 1}">
            <div style="display:inline-block;">
                <a title="Click to order" id="editAgentPriorityOrder" class="btn">
                    <span class="icon_before icon16"
                          style="background-repeat: no-repeat; background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACYUlEQVR42o2RXUiTURjHn/Pu3XvO2bu5uU3n2tDILMukD+jCSERj0geYdBFEFBJJadJFRaPySpANIrswpYRaFxORyoQ+iCLJgi4qiOgDvNIQQSvF+W64r/eczgitvNj8w8M5D//n/M7znINghaoCg/vMKj0BwDerRF105Nve8mT8+p2ju8czftPtl/bo7A/9/sUjkUyOlg6WHfcj8/baxkRUq9RT8VaWTLgUBQPGFBw2K1Ru2nCrq67kdMvTiZ+yUW7r9nkH/wOsVEXgoR/FY0FCKBAjBqOAeVxOFk2AVLq+7Py1XeaurICMKjsGNmIZfyRYpkQxgYIxUGKCIm9Jd0+d82xOQEZbr4Rcar5jihiJgVAKWCFQsMb7qK/e3bAqwI7AUI0ioVdEEaNkAJiAvdA9FjpQXJ4TUNhwUi3aWR9VVcssNakz4j1mTGJvcbqHQrXWgZyAxp5ntuEze+ez1SwD/B/iHQlt7mAynXbqKVbIGJOF0t9Gn5DRQIueE7D/7vvxRGxh7d+WpBTi6dIXbb7JVXVQfWPkeSqm+ZYN4eicl7/zN46tCrAtONyXikaa/zUlSQKgas3nS4de5wRUdN67rMe0TgZoBnHuQmipEwQGqt780n64JSug+FzvMaSYWr8Hm6q2tIcuRFL8qlG4EvpTxCRDQqbmAU+B7fHIqT0PlgHhcNhiz7eVTP6a9zCd0yIKBp0Bnopzx9cF1jwZ42VpwXFjNF1hRb1eAp9UShdlaprTNG0C9ff3r+OcV4tbXAKYxwEsYuVi+jwZgS46YJmccVjQORjFf8ZEromYFvHmNxDCyyGqSfPRAAAAAElFTkSuQmCC'); display:block">
                        Reorder
                    </span>
                </a>
            </div>
        </c:if>

        <c:if test="${inherited}">
            <br/>
            <p>Agent priorities inherited from <admin:editProjectLink projectId="${project.externalId}"><c:out
                    value="${project.name}"/></admin:editProjectLink>:</p>
        </c:if>

        <bs:refreshable containerId="prioritiesTable" pageUrl="${pageUrl}">

            <bs:messages key="priorityAdded"/>
            <bs:messages key="priorityUpdated"/>
            <bs:messages key="priorityRemoved"/>

            <l:tableWithHighlighting className="parametersTable" highlightImmediately="true" style="width:70%;">
                <tr>
                    <th style="width: 30%">Priority</th>
                    <c:if test="${!inherited}">
                        <th colspan="2">Actions</th>
                    </c:if>
                </tr>
                <c:forEach items="${projectPriorities}" var="priority">
                    <c:choose>
                        <c:when test="${inherited}">
                            <tr>
                                <td><i><c:out value='${priority.agentPriority.name}'/></i></td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:set var="onclick"
                                   value="BS.AgentPriorityDialog.showEditDialog('${priority.id}', '${priority.agentPriority.name}', ${currentProject.readOnly})"/>
                            <tr>
                                <td class="highlight" onclick="${onclick}"><c:out
                                        value='${priority.agentPriority.name}'/></td>
                                <c:if test="${not inherited}">
                                    <td class="edit highlight" onclick="${onclick}">
                                        <a href="#"
                                           onclick="${onclick}">${currentProject.readOnly ? 'View' : 'Edit'}</a>
                                    </td>
                                    <c:if test="${not currentProject.readOnly}">
                                        <td class="edit">
                                            <a href="#"
                                               onclick="BS.AgentPriority.deletePriority('${prioritiesUrl}', '${priority.id}', '${currentProject.externalId}')">Delete</a>
                                        </td>
                                    </c:if>
                                </c:if>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </l:tableWithHighlighting>


            <div style="margin-top: 30px;">
                <p>Here you can view the final order of agents for the selected build.</p>
                <forms:select name="buildSelector" enableFilter="true"
                              onchange="BS.AgentPriority.getBuildAgentsForBuild('${prioritiesUrl}', this, '${currentProject.externalId}')"
                              className="longField">
                    <option value="">-- Select build for viewing agent order --</option>
                    <c:forEach items="${availableBuilds}" var="build">
                        <option value="${build.buildTypeId}"><c:out value="${build.name}"/></option>
                    </c:forEach>
                </forms:select>

                <ol id="agentsForBuild"></ol>
            </div>

        </bs:refreshable>
    </c:forEach>


    <bs:modalDialog formId="AgentPriority" title="Add Priority" saveCommand="BS.AgentPriorityDialog.save()"
                    closeCommand="BS.AgentPriorityDialog.close()"
                    action="${prioritiesUrl}">
        <table class="runnerFormTable" style="width: 99%;">
            <tr>
                <td>
                    <label>Priority type: </label>
                </td>
                <td>
          <span id="priorityType">
            <forms:select name="typeSelector" enableFilter="true"
                          onchange="BS.AgentPriorityDialog.priorityChanged(this)" className="longField"
                          style="width:28em">
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
            <input type="hidden" name="operation" value="savePriority"/>
        </div>
    </bs:modalDialog>

    <c:if test="${!inherited && projectPriorities.size() > 1}">
        <bs:reorderDialog dialogId="reorderAgentPrioritiesDialog" dialogTitle="Agent Priorities">
                        <jsp:attribute name="sortables">
                        <c:forEach items="${projectPriorities}" var="priority">
                        <div class="draggable tc-icon_before icon16 tc-icon_draggable"
                             id="ord_${priority.id}">
                            <c:out value="${priority.agentPriority.name}"/></div>
                        </c:forEach>
                        </jsp:attribute>
            <jsp:attribute name="actionsExtension">
                        </jsp:attribute>
        </bs:reorderDialog>
    </c:if>

    <script type="text/javascript">
        $j(function () {
            BS.AgentPriorityOrderDialog.createReorderDialog("${currentProject.externalId}");
        });
    </script>

</div>