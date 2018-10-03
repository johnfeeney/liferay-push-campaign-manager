package com.jfeeney.campaign.portlet.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

import com.jfeeney.campaign.constants.CampaignPushManagerPortletKeys;
import com.liferay.content.targeting.anonymous.users.model.AnonymousUser;
import com.liferay.content.targeting.model.UserSegment;
import com.liferay.content.targeting.service.AnonymousUserUserSegmentLocalService;
import com.liferay.content.targeting.service.AnonymousUserUserSegmentLocalServiceUtil;
import com.liferay.content.targeting.service.UserSegmentLocalService;
import com.liferay.content.targeting.service.UserSegmentLocalServiceUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.push.notifications.model.PushNotificationsDevice;
import com.liferay.push.notifications.service.PushNotificationsDeviceLocalService;
import com.liferay.push.notifications.service.PushNotificationsDeviceLocalServiceUtil;

/**
 * @author jfeeney
 */

@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + CampaignPushManagerPortletKeys.CAMPAIGN_PUSH_MANAGER,
				"mvc.command.name=/push_campaign_message"
		},
		service = MVCActionCommand.class
)
public class CampaignPushMVCActionCommand extends BaseMVCActionCommand {
	
	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		
		userSegmentService = UserSegmentLocalServiceUtil.getService();
		pushService = PushNotificationsDeviceLocalServiceUtil.getService();
		
		
		String pushMessage = actionRequest.getParameter("push-message");
		String selectedCampaign = actionRequest.getParameter("select-campaign");
		String msgTitle = actionRequest.getParameter("message-title");
		String msgBody = actionRequest.getParameter("message-body");
		
		
		_log.error("pushMessage = " + pushMessage);
		_log.error("selectedCampaign = " + selectedCampaign);
		_log.error("msgTitle = " + msgTitle);
		_log.error("msgBody = " + msgBody);
		
		
		boolean sendPushMessage = Boolean.valueOf(pushMessage);
		
		if (sendPushMessage) {
			long campaignId = Long.valueOf(selectedCampaign);			
			List<UserSegment> userSegments = userSegmentService.getCampaignUserSegments(campaignId);
			
			if (userSegments != null) {
				List<Long> campaignUserIds = getUserIdsFromSegments(userSegments);

				if (campaignUserIds.size() > 0) {
					List<PushNotificationsDevice> devices = pushService.getPushNotificationsDevices(0, 999);					
					List<Long> targetDeviceUserIds = getRegisteredDevicesFromCampaignUsers(campaignUserIds, devices);
					
					if (targetDeviceUserIds.size() > 0) {
						try {
							sendPushNotification(targetDeviceUserIds, msgTitle, msgBody);
						}
						catch (Exception e) {
							_log.error("Could not send message", e);
						}
					}
				}
			}
		}	
	}
	
	private List<Long> getUserIdsFromSegments(List<UserSegment> segments) throws Exception {
		
		List<Long> targetUserIds = new ArrayList<Long>();
		List<AnonymousUser> tmpUsers = new ArrayList<AnonymousUser>();
		
		AnonymousUserUserSegmentLocalService anonService = AnonymousUserUserSegmentLocalServiceUtil.getService();
		for (UserSegment segment: segments) {
			tmpUsers = anonService.getAnonymousUsersByUserSegmentId(segment.getUserSegmentId(), true);
			for (AnonymousUser tmpUser: tmpUsers) {
				targetUserIds.add(tmpUser.getUserId());
			}
		}	
		return targetUserIds;
	}
	
	private List<Long> getRegisteredDevicesFromCampaignUsers(List<Long> campaignUserIds, List<PushNotificationsDevice> devices) throws Exception {
		List<Long> targetDeviceUserIds = new ArrayList<Long>();
		for (PushNotificationsDevice device: devices) {
			for (Long campaignUserId: campaignUserIds) {
				if (campaignUserId == device.getUserId())
					targetDeviceUserIds.add(device.getUserId());
			}
		}
		return targetDeviceUserIds;
	}
	
	private void sendPushNotification(List<Long> targetDeviceUserIds, String msgTitle, String msgBody) throws Exception {
		
		//convert List<Long> to long[]
		long[] deliveryDeviceUserIds = new long[targetDeviceUserIds.size()];
		for (int i = 0; i < targetDeviceUserIds.size(); i++) {
			deliveryDeviceUserIds[i] = (long) targetDeviceUserIds.get(i);
		}
		
		if (_log.isDebugEnabled()) {
			for (int i = 0; i < deliveryDeviceUserIds.length; i++) {
				_log.error("deliveryDeviceUserIds[" + i + "]: " + deliveryDeviceUserIds[i]);
			}
		}
		
		StringBuilder payloadString = new StringBuilder();
		payloadString.append("{\"body\":\"{\\\"title\\\":\\\"").append(msgTitle);
		payloadString.append("\\\", \\\"description\\\":\\\"").append(msgBody);
		payloadString.append("\\\"}\"}");
						
		JSONObject payload = JSONFactoryUtil.createJSONObject(payloadString.toString());
		pushService.sendPushNotification(deliveryDeviceUserIds, payload);
	}	
	
	
		
	UserSegmentLocalService userSegmentService;
	PushNotificationsDeviceLocalService pushService;
	
	private static final Log _log = LogFactoryUtil.getLog(CampaignPushMVCActionCommand.class);
}
