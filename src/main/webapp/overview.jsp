<%@ taglib uri="http://myfaces.apache.org/tobago/component" prefix="tc" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<f:view>
  <tc:page>
    <f:facet name="layout">
      <tc:gridLayout columns="1*;3*"/>
    </f:facet>

    <tc:box label="myBox" id="myBox1">
      <f:facet name="layout">
        <tc:gridLayout/>
      </f:facet>
      <tc:tree idReference="userObject.id" state="#{controller.treeState}" id="myTree" value="#{controller.folderView}" nameReference="userObject.name">
        <f:facet name="treeNodeCommand">
          <tc:link action="#{controller.treeSelected}" />
        </f:facet>
      </tc:tree>
    </tc:box>


    <tc:box label="myBox" id="myBox2">
      <f:facet name="layout">
        <tc:gridLayout rows="1*;1*"/>
      </f:facet>
      <tc:sheet id="mySheet" value="#{controller.messages}" columns="30px;1*;2*" var="message" state="#{controller.sheetState}" stateChangeListener="#{controller.sheetSelected}">
        <tc:column label="#" id="id" sortable="true" align="center">
          <tc:out value="#{message.messageNumber}" id="t_number"/>
        </tc:column>
        <tc:column label="From" id="from" sortable="true">
          <tc:out value="#{message.from[0]}" id="t_from"/>
        </tc:column>
        <tc:column label="Subject" id="subject" sortable="true">
          <tc:out value="#{message.subject}" id="t_subject"/>
        </tc:column>
      </tc:sheet>
      <tc:textarea readonly="true" value="#{controller.selectedFolder.name}">
      </tc:textarea>
    </tc:box>

  </tc:page>
</f:view>
