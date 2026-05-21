package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse;

public interface ReadingStatsManagementService {
    UserReadingStatResponse getUserReadingStats(String userId);
}
