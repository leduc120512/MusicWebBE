package com.musicapi.service;

import com.musicapi.dto.DashboardStatsResponse;
import com.musicapi.model.ArtistRequestStatus;
import com.musicapi.model.CommentReportStatus;
import com.musicapi.model.Role;
import com.musicapi.model.ViolationReportStatus;
import com.musicapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SongViolationReportRepository songViolationReportRepository;

    @Autowired
    private CommentReportRepository commentReportRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRegistrationRequestRepository artistRequestRepository;

    public DashboardStatsResponse getMyArtistStats(Long artistId) {
        DashboardStatsResponse response = new DashboardStatsResponse();
        response.setTotalSongs(nvl(songRepository.countByArtist_Id(artistId)));
        response.setTotalAlbums(nvl(albumRepository.countByArtist_Id(artistId)));
        response.setTotalComments(nvl(commentRepository.countBySong_Artist_IdAndDeletedFalse(artistId)));
        response.setTotalSongReports(nvl(songViolationReportRepository.countBySong_Artist_Id(artistId)));
        response.setTotalCommentReports(nvl(commentReportRepository.countByComment_Song_Artist_Id(artistId)));
        response.setTotalLikes(nvl(likeRepository.countBySong_Artist_Id(artistId)));
        response.setTotalPlayCount(nvl(songRepository.sumPlayCountByArtistId(artistId)));
        return response;
    }

    public DashboardStatsResponse getGlobalAdminStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        response.setTotalSongs(songRepository.count());
        response.setTotalAlbums(albumRepository.count());
        response.setTotalComments(nvl(commentRepository.countByDeletedFalse()));
        response.setTotalSongReports(songViolationReportRepository.count());
        response.setTotalCommentReports(commentReportRepository.count());
        response.setTotalLikes(likeRepository.count());
        response.setTotalPlayCount(nvl(songRepository.sumAllPlayCount()));

        response.setTotalUsers(userRepository.count());
        response.setTotalAuthors(nvl(userRepository.countByRole(Role.ROLE_AUTHOR)));
        response.setTotalArtistRequests(artistRequestRepository.count());
        response.setPendingArtistRequests(nvl(artistRequestRepository.countByStatus(ArtistRequestStatus.PENDING)));
        return response;
    }

    public DashboardStatsResponse getGlobalPendingOnlyAdminStats() {
        DashboardStatsResponse response = getGlobalAdminStats();
        response.setTotalSongReports(nvl(songViolationReportRepository.countByStatus(ViolationReportStatus.PENDING)));
        response.setTotalCommentReports(nvl(commentReportRepository.countByStatus(CommentReportStatus.PENDING)));
        return response;
    }

    private Long nvl(Long value) {
        return value == null ? 0L : value;
    }
}

