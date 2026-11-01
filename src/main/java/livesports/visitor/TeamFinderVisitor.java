package livesports.visitor;

import livesports.tree.*;

import java.util.List;
import java.util.Optional;

public final class TeamFinderVisitor implements Visitor<Optional<Team>> {

    private final String teamId;

    public TeamFinderVisitor(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public Optional<Team> visitLeague(League league) { return firstFound(league.clubs()); }

    @Override
    public Optional<Team> visitClub(Club club) { return firstFound(club.teams()); }

    @Override
    public Optional<Team> visitTeam(Team team) {
        return team.id().equals(teamId) ? Optional.of(team) : Optional.empty();
    }

    @Override
    public Optional<Team> visitPlayer(Player player) { return Optional.empty(); }

    @Override
    public Optional<Team> visitCoach(Coach coach) { return Optional.empty(); }

    @Override
    public Optional<Team> visitPhysio(Physio physio) { return Optional.empty(); }

    @Override
    public Optional<Team> visitAnalyst(Analyst analyst) { return Optional.empty(); }

    private Optional<Team> firstFound(List<? extends Element> children) {
        for (Element child : children) {
            Optional<Team> found = child.accept(this);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }
}