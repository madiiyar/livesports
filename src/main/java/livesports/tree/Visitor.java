package livesports.tree;

public interface Visitor<R> {
    R visitLeague(League league);
    R visitClub(Club club);
    R visitTeam(Team team);
    R visitPlayer(Player player);
    R visitCoach(Coach coach);
    R visitPhysio(Physio physio);
    R visitAnalyst(Analyst analyst);
}