package livesports.listener;

import livesports.event.TeamDisciplineEvent;
import livesports.observer.Observer;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class DisciplineAlertListener implements Observer<TeamDisciplineEvent> {

    private final int threshold;
    private final Queue<String> alerts = new ConcurrentLinkedQueue<>();

    public DisciplineAlertListener(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void onEvent(TeamDisciplineEvent event) {
        if (event.totalPoints() >= threshold) {
            alerts.add("Team " + event.teamId() + " reached " + event.totalPoints()
                    + " discipline points");
        }
    }

    public List<String> alerts() {
        return List.copyOf(alerts);
    }
}