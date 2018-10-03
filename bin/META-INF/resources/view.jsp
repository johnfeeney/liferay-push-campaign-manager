<%@ include file="/init.jsp" %>

<%
	String namespace = themeDisplay.getPortletDisplay().getNamespace();
%>
<script>
(function() {
  $(document).ready(function() {
    $('.toggle-switch').on('change', function() {
      var isChecked = $(this).is(':checked');
      var selectedData;
      var $switchLabel = $('.toggle-switch-label');

      if(isChecked) {
    	$('#<%=namespace%>push-message').val("true");    
      } 
      else {
    	$('#<%=namespace%>push-message').val("false");
      }
      console.log('push-message:' + $('#<%=namespace%>push-message').val());
    });
  });

})();
</script>

<portlet:actionURL name="/push_campaign_message" var="campaignPushURL">
	<portlet:param name="mvcActionCommand" value="/push_campaign_message" />
</portlet:actionURL>

<h2>Campaign Creation Page</h2>
<p>Create your campaign.</p>

<aui:form action="<%= campaignPushURL %>" method="post" name="fm" >

        <aui:fieldset-group markupView="lexicon">
                <aui:fieldset>
						<aui:select name="select-campaign" label="select-campaign-label" required="true" >
						 	<c:forEach items="${campaigns}" var="campaign">
						 	<aui:option label="${campaign.name}" value="${campaign.campaignId}" />
						 	</c:forEach>
						</aui:select>	
                </aui:fieldset>
		</aui:fieldset-group>
		
		<aui:fieldset-group markupView="lexicon">
                <aui:fieldset>
                		<aui:input name="push-message" type="hidden" value="false" />
						<div class="form-group">						  
						    <label>
						        <input class="toggle-switch" type="checkbox">
						        <span class="toggle-switch-label">Push message?</span>
						
						        <span aria-hidden="true" class="toggle-switch-bar">
						            <span class="toggle-switch-handle" data-label-off="" data-label-on="ON">
						            </span>
						        </span>
						    </label>
						</div>
						
                        <aui:input name="message-title" label="message-title-label" placeholder="Message title goes here..." type="text">
							<aui:validator name="maxLength">75</aui:validator>
                        </aui:input> 
                        <aui:input name="message-body" label="message-body-label" placeholder="Message body goes here..." type="text">
							<aui:validator name="maxLength">75</aui:validator>
                        </aui:input>                           
                </aui:fieldset>     
                           
        </aui:fieldset-group>

        <aui:button-row>
                <aui:button type="submit" />
                <aui:button href="#" type="cancel" />
        </aui:button-row>
        
</aui:form>