package org.magnum.mobilecloud.video.repository;

import java.util.Collection;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * An interface for a repository that can store Video
 * objects and allow them to be searched by title.
 * 
 * @author jules
 *
 */
@RepositoryRestResource(path = VideoSvcApi.VIDEO_SVC_PATH)
public interface VideoRepository extends CrudRepository<Video, Long>{
	
	public static final String QUERY_DURATION = "select v from Video v where v.duration < :duration";
	public static final String QUERY_TITLE = "select v from Video v where v.name like :title";
	
	@Query(QUERY_TITLE)
	public Collection<Video> findByName(
			@Param(VideoSvcApi.TITLE_PARAMETER) String title);
	
	@Query(QUERY_DURATION)
	public Collection<Video> findByDurationLessThan(
			// The @Param annotation tells tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "duration" variable used to
			// search for Videos
			@Param(VideoSvcApi.DURATION_PARAMETER) long maxduration);
}
