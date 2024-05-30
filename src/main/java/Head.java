import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Head {
    private int index;
    private long timestamp;
    private String previousHash;
    private String merkleRootHash;
    private long nonce;
}
