package pl.app.common.shared.model;


public class Progress {
    private Long exp;

    public Progress() {
        this.exp = 0L;
    }

    public Progress(Long exp) {
        this.exp = exp;
    }

    public void multiply(Long n) {
        this.exp = this.exp * n;
    }

    public Long getExp() {
        return exp;
    }
}
