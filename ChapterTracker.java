public class ChapterTracker {
    private int chapterNumber;
    private Character povCharacter; // Association
    private String plotBeat;
    private String chapterSummary;

    public int getChapterNumber() {return chapterNumber;}
    public void setChapterNumber(int num) {this.chapterNumber = num;}
    public Character getPovCharacter() {return povCharacter;}
    public void setPovCharacter(Character povCharacter) {this.povCharacter = povCharacter;}
    public String getPlotBeat() {return plotBeat;}
    public void setPlotBeat(String beat) {this.plotBeat = beat;}
    public String getChapterSummary() {return chapterSummary;}
    public void setChapterSummary(String summary) {this.chapterSummary = summary;}
}
