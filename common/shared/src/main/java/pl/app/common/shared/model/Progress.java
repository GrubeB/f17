package pl.app.common.shared.model;


public class Progress {
    private Long exp;

    public Progress() {
        this.exp = 0L;
    }

    public Progress(Long exp) {
        this.exp = exp;
    }

    public Progress add(Long n) {
        return new Progress(this.exp + n);
    }

    public Progress add(Progress p) {
        return new Progress(this.exp + p.getExp());
    }

    public Progress multiply(Long n) {
        return new Progress(this.exp * n);
    }

    public Long getExp() {
        return exp;
    }
}
