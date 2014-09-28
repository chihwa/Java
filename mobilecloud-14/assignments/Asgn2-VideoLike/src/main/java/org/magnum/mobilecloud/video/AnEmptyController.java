/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

@Controller
public class AnEmptyController {
	
	@Autowired
	VideoRepository videos;
	Video video = new Video();
	public static final String TITLE_PARAMETER = "title";
	public static final String DURATION_PARAMETER = "duration";

	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v){
		Video video = videos.save(v);
		String url = this.getDataUrl(video.getId());
		video.setUrl(url);
		return v;
	}
	
	private String getUrlBaseForLocalServer() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String base = "http://" + request.getServerName()
				+ ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
		return base;
	}
	
	private String getDataUrl(long videoId) {
		String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
		return url;
	}
	
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		return Lists.newArrayList(videos.findAll());
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id, HttpServletResponse response){
		Video video = null;
		try {
			video = videos.findOne(id);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return video;
	}
	
	@RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(
			@RequestParam(TITLE_PARAMETER) String title) {
		return videos.findByName(title);
	}
	
	@RequestMapping(value = VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(
			@RequestParam(DURATION_PARAMETER) long duration) {
		return videos.findByDurationLessThan(duration);
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody void likeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal user) throws IOException {
		if (!videos.exists(id)) {
			response.sendError(404);
			return;
		}
		String username = user.getName();
		Video v = videos.findOne(id);
		Set<String> likesUsernames = v.getLikesUsernames();
		List<String> happyUsers = v.getLikedVideo();
		// Checks if the user has already liked the video.
		if (likesUsernames.contains(username)) {
			response.sendError(400);
			return;
		} else {
			long likes = v.getLikes();
			v.setLikes( ++likes);
			v.getLikesUsernames().add(username);
		}
		if (!happyUsers.contains(username)) {
			happyUsers.add(username);
			v.setLikedVideo(happyUsers);
		}
		videos.save(v);
	}

	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody void unlikeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal user) throws IOException {
		if (!videos.exists(id)) {
			response.sendError(404);
			return;
		}
		String username = user.getName();
		Video v = videos.findOne(id);
		Set<String> unlikesUsernames = v.getUnlikesUsernames();
		List<String> happyUsers = v.getLikedVideo();
		// Checks if the user has already liked the video.
		if (unlikesUsernames.contains(username)) {
			response.sendError(400);
			return;
		} else {
			long likes = v.getLikes();
			v.setLikes( --likes);
			v.getUnlikesUsernames().add(username);
		}
		if (happyUsers.contains(username)) {
			happyUsers.remove(username);
			v.setLikedVideo(happyUsers);
		}
		videos.save(v);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/likedby", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Collection<String> getUsersWhoLikedVideo
	(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
		Collection<String> happyUsers = new ArrayList<String>();
		if (videos.exists(id)) {
			Video v = videos.findOne(id);
			happyUsers = v.getLikedVideo();
		} else {
			response.sendError(404);	
		}
		return happyUsers;
	}

	
}
