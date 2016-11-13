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
    getContainer: function() {
        return $('AgentPriorityDialog');
    },

    formElement: function() {
        return $('AgentPriority');
    },

    savingIndicator: function() {
        return $('saveProgress');
    },

    showAddDialog: function(type) {
        this.enable();
        $j('#AgentPriorityTitle').text('Add agent priority');
        $j('#priorityType').show();
        $j('#readOnlyPriorityType').hide();
        this.formElement().priorityId.value = '';
        this.formElement().afterAddUrl.value = '<bs:escapeForJs text="${afterAddUrl}"/>';
        $('typeSelector').setSelectValue(type == null ? '' : type);
        this.priorityChanged($('typeSelector'));
        this.showCentered();
    },

    priorityChanged: function(selector) {
        this.formElement().priorityType.value = '';
        $j('#connectionParams').html('');
        if (selector.selectedIndex > 0) {
            this.formElement().priorityType.value = selector.options[selector.selectedIndex].value;
            // this.loadParameters();
        }
    }
}));