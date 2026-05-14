public class Chapter extends StoryElement {
    private int chapterNumber;
    private String povCharacterId;
    private String plotBeat;
    private String chapterSummary;

    public Chapter(String id, String name, String description, int chapterNumber) {
        super(id, name, description);
        this.chapterNumber = chapterNumber;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getPovCharacterId() {
        return povCharacterId;
    }

    public void setPovCharacterId(String povCharacterId) {
        this.povCharacterId = povCharacterId;
    }

    public String getPlotBeat() {
        return plotBeat;
    }

    public void setPlotBeat(String plotBeat) {
        this.plotBeat = plotBeat;
    }

    public String getChapterSummary() {
        return chapterSummary;
    }

    public void setChapterSummary(String chapterSummary) {
        this.chapterSummary = chapterSummary;
    }
}
