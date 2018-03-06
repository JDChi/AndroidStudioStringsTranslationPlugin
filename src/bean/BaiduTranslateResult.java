package bean;
import java.util.List;

public class BaiduTranslateResult {

    private String from;
    private String to;
    private List<Trans_result> trans_result;
    public void setFrom(String from) {
        this.from = from;
    }
    public String getFrom() {
        return from;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public String getTo() {
        return to;
    }

    public void setTrans_result(List<Trans_result> trans_result) {
        this.trans_result = trans_result;
    }
    public List<Trans_result> getTrans_result() {
        return trans_result;
    }

    public class Trans_result {

        private String src;
        private String dst;
        public void setSrc(String src) {
            this.src = src;
        }
        public String getSrc() {
            return src;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }
        public String getDst() {
            return dst;
        }

    }
}