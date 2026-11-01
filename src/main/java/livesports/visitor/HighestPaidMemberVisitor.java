package livesports.visitor;

import livesports.tree.*;

import java.util.List;
import java.util.Optional;

public final class HighestPaidMemberVisitor implements Visitor<Optional<Member>> {

    @Override
    public Optional<Member> visitLeague(League league) { return best(league.clubs()); }

    @Override
    public Optional<Member> visitClub(Club club) { return best(club.teams()); }

    @Override
    public Optional<Member> visitTeam(Team team) { return best(team.members()); }

    @Override
    public Optional<Member> visitPlayer(Player player) { return Optional.of(player); }

    @Override
    public Optional<Member> visitCoach(Coach coach) { return Optional.of(coach); }

    @Override
    public Optional<Member> visitPhysio(Physio physio) { return Optional.of(physio); }

    @Override
    public Optional<Member> visitAnalyst(Analyst analyst) { return Optional.of(analyst); }

    @Override
    public Optional<Member> visitScout(Scout scout) { return Optional.of(scout); }

    private Optional<Member> best(List<? extends Element> children) {
        Optional<Member> best = Optional.empty();
        for (Element child : children) {
            Optional<Member> candidate = child.accept(this);
            if (candidate.isPresent()
                    && (best.isEmpty() || candidate.get().salary() > best.get().salary())) {
                best = candidate;
            }
        }
        return best;
    }
}