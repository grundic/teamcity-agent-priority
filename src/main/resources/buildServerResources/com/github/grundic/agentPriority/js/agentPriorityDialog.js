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

BS.AgentPriorityDialog = OO.extend(BS.PluginPropertiesForm, OO.extend(BS.AbstractModalDialog, {
    getContainer: function () {
        return $('AgentPriorityDialog');
    },

    formElement: function () {
        return $('AgentPriority');
    },

    savingIndicator: function () {
        return $('saveProgress');
    },

    showAddDialog: function (type) {
        this.enable();
        $j('#AgentPriorityTitle').text('Add agent priority');
        $j('#priorityType').show();
        $j('#readOnlyPriorityType').hide();
        this.formElement().priorityId.value = '';
        $('typeSelector').setSelectValue(type == null ? '' : type);
        this.priorityChanged($('typeSelector'));
        this.showCentered();
    },

    showEditDialog: function (priorityId, name, readOnly) {
        this.enable();
        $j('#AgentPriorityTitle').text('Edit Priority');
        $j('#priorityType').hide();
        $j('#readOnlyPriorityType').show();
        $j('#readOnlyPriorityType').text(name);
        this.formElement().priorityType.value = '';
        this.formElement().priorityId.value = priorityId;
        this.loadParameters(readOnly);
        this.showCentered();
    },

    priorityChanged: function (selector) {
        this.formElement().priorityType.value = '';
        $j('#priorityParams').html('');
        if (selector.selectedIndex > 0) {
            this.formElement().priorityType.value = selector.options[selector.selectedIndex].value;
            this.loadParameters();
        }
    },

    loadParameters: function (readOnly) {
        $('parametersProgress').show();
        var that = this;
        BS.ajaxUpdater('priorityParams', window['base_uri'] + '/admin/teamcity-agent-priority/configuration.html', {
            parameters: 'priorityType=' + this.formElement().priorityType.value + "&projectId=" + this.formElement().projectId.value + "&priorityId=" + this.formElement().priorityId.value,
            evalScripts: true,
            onComplete: function () {
                $('parametersProgress').hide();

                if (readOnly) {
                    that.disable();
                }
                that.recenterDialog();
            }
        });
    },


    save: function () {
        if (this.formElement().priorityId.value == '' && $('typeSelector').selectedIndex <= 0) {
            alert("Please select some agent priority");
            return false;
        }

        BS.FormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener, {
            onCompleteSave: function (form, responseXML, err) {
                err = BS.XMLResponse.processErrors(responseXML, {}, BS.PluginPropertiesForm.propertiesErrorsHandler);

                form.setSaving(false);
                if (err) {
                    form.enable();
                } else {
                    if (!BS.XMLResponse.processRedirect(responseXML)) {
                        $('prioritiesTable').refresh();
                        BS.AgentPriorityDialog.close();
                    }
                }
            }
        }));
        return false;
    }
}));

BS.AgentPriority = {
    deletePriority: function (url, priorityId, projectId) {
        if (!confirm("Are you sure you want to delete this agent priority?")) return;

        BS.ajaxRequest(url, {
            parameters: 'operation=deletePriority' + '&priorityId=' + priorityId + "&projectId=" + projectId,

            onComplete: function () {
                $('prioritiesTable').refresh();
            }
        });
    }
};