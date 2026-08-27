package com.musicapi.service;

import com.musicapi.dto.DashboardStatsResponse;
import com.musicapi.model.ArtistRequestStatus;
import com.musicapi.model.CommentReportStatus;
import com.musicapi.model.Role;
import com.musicapi.model.ViolationReportStatus;
import com.musicapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final CommentRepository commentRepository;
    private final SongViolationReportRepository songViolationReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ArtistRegistrationRequestRepository artistRequestRepository;

    public DashboardService(
            SongRepository songRepository,
            AlbumRepository albumRepository,
            CommentRepository commentRepository,
            SongViolationReportRepository songViolationReportRepository,
            CommentReportRepository commentReportRepository,
            LikeRepository likeRepository,
            UserRepository userRepository,
            ArtistRegistrationRequestRepository artistRequestRepository
    ) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.commentRepository = commentRepository;
        this.songViolationReportRepository = songViolationReportRepository;
        this.commentReportRepository = commentReportRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.artistRequestRepository = artistRequestRepository;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

