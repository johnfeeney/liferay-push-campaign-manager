package com.jfeeney.campaign.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.jfeeney.campaign.constants.CampaignPushManagerPortletKeys;
import com.jfeeney.campaign.portlet.action.CampaignPushMVCActionCommand;
import com.liferay.content.targeting.model.Campaign;
import com.liferay.content.targeting.service.CampaignLocalService;
import com.liferay.content.targeting.service.CampaignLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;


/**
 * @author jfeeney
 */

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Campaign Push Manager Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CampaignPushManagerPortletKeys.CAMPAIGN_PUSH_MANAGER,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class CampaignPushManagerPortlet extends MVCPortlet {

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		
		CampaignLocalService service = CampaignLocalServiceUtil.getService();
		List<Campaign> campaigns = new ArrayList<Campaign>();
		
		ThemeDisplay themeDisplay= (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getSiteGroupId();
		
		try {
			campaigns = service.getCampaigns(groupId);
		} 
		catch (Exception e) {
			_log.error(e);
		}
		
		renderRequest.setAttribute("campaigns", campaigns);

		super.render(renderRequest, renderResponse);
	}	
	
	private static final Log _log = LogFactoryUtil.getLog(CampaignPushMVCActionCommand.class);
	
}