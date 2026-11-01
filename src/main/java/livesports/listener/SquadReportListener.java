package livesports.listener;

import livesports.event.MatchEvent;
import livesports.event.SquadReportEvent;
import livesports.observer.AbstractSubject;
import livesports.observer.Observer;
import livesports.tree.Element;
import livesports.tree.Team;
import livesports.visitor.RosterStatsVisitor;
import livesports.visitor.TeamFinderVisitor;

import java.util.Optional;

public final class SquadReportListener extends AbstractSubject<SquadReportEvent>
        implements Observer<MatchEvent> {

    private final Element root;

    public SquadReportListener(Element root) {
        this.root = root;
    }

    @Override
    public void onEvent(MatchEvent goal) {
        Optional<Team> team = root.accept(new TeamFinderVisitor(goal.teamId()));
        if (team.isEmpty()) {
            return;                                   // unknown team: nothing to report
        }
        RosterStatsVisitor stats = new RosterStatsVisitor();
        team.get().accept(stats);                     // visitor runs on ONE team subtree only
        publish(new SquadReportEvent(goal.teamId(), stats.playerCount(),
                stats.totalGoals(), stats.averageRating()));
    }
}