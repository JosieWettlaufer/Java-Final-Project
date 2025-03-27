package com.example.GridFSDemo.scheduler;

import com.example.GridFSDemo.model.TimeCapsule;
import com.example.GridFSDemo.repository.TimeCapsuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CapsuleNotificationScheduler {

    @Autowired
    private TimeCapsuleRepository capsuleRepository;

    // Run every day at 8 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void checkAndNotifyUnlockCapsules() {
        LocalDate today = LocalDate.now();
        List<TimeCapsule> dueCapsules = capsuleRepository.findByLockDate(today);

        for (TimeCapsule capsule : dueCapsules) {
            if (!capsule.isUnlocked()) {
                capsule.setUnlocked(true);
                capsuleRepository.save(capsule);
                // 🔔 Later: send email notification
                System.out.println("Capsule unlocked: " + capsule.getId());
            }
        }
    }
}