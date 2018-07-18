package SubProcess;

public enum Status {
    /**
     * server is ready to go
     */
    READY
    /**
     *server are running
     */
    , RUNNING
    /**
     *server stopped
     */
    , STOPPED,
    /**
     * server is stopping
     */
    STOPPING,
    /**
     * server are waiting for restart
     */
    RESTARTING,
    /**
     * server are error and need to restart
     */
    ERROR,
    /**
     * server has been force stopped
     */
    FORCE_STOP,
    /**
     * the server is starting but not ready for player
     */
    STARTING
}
