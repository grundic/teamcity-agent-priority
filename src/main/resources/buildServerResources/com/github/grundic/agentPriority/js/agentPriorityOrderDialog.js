/*
 * The MIT License
 *
 * Copyright (c) 2016 Grigory Chernyshev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

'use strict';

BS.AgentPriorityOrderDialog = {
    createReorderDialog: function (projectId) {
        var $reorderAgentPrioritiesDialog = $j("#reorderAgentPrioritiesDialog");

        var form = BS.createReorderDialog('reorderAgentPrioritiesDialog', $j("#sortableList"), function (order) {
            form.setDisabled(true);

            BS.ajaxRequest(window["base_uri"] + "/admin/teamcity-agent-priority/priorities.html", {
                parameters: {
                    operation: 'reorderPriority',
                    priorityOrder: order,
                    projectId: projectId
                },
                method: "POST",
                onComplete: function () {
                    form.setDisabled(false);
                    form.close();
                    BS.reload();
                }
            });
        });

        $reorderAgentPrioritiesDialog.bind("closeDialog", function () {
            form.resetState();
        });

        $j('#editAgentPriorityOrder').click(function () {
            form.showCentered();
        });
    }
};
