<%@ taglib uri="http://myfaces.apache.org/tobago/component" prefix="tc" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<f:view>
  <tc:page>
    <f:facet name="layout">
      <tc:gridLayout rows="110px"/>
    </f:facet>

    <tc:box label="myBox" id="myBox1">
      <f:facet name="layout">
        <tc:gridLayout columns="1*;1*"/>
      </f:facet>
      <tc:out id="label_username" value="username"></tc:out>
      <tc:in label="username" value="#{controller.username}" required="true"></tc:in>
      <tc:out id="label_password" value="password"></tc:out>
      <tc:in label="password" value="#{controller.password}" required="true" password="true"></tc:in>
      <tc:out id="label_empty" value=""></tc:out>
      <tc:button label="LogIn!" action="#{controller.login}"></tc:button>
    </tc:box>

  </tc:page>
</f:view>
