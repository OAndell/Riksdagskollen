package oscar.riksdagskollen.Util.WebTV;

public interface DebatePlayer {
    void pause();

    void play();

    void fastForward();

    void fastRewind();

    void seekTo(int seconds);
}
