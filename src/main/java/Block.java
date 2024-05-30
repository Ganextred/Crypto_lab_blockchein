import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Block {
    private Head head;
    private List<Transaction> transactions;

    public Block(List<Transaction> transactions){
        String merkleRootHash = calculateMerkleRootHash(transactions);
        this.head = Head.builder().merkleRootHash(merkleRootHash).build();
        this.transactions = new ArrayList<>(transactions);
    }

    public static String calculateMerkleRootHash(List<Transaction> transactions) {
        if (transactions.size() == 0)
            return Blockchain.calculateHash(null);
        List<String> hashes = new ArrayList<>();
        for (Transaction transaction : transactions) {
            hashes.add(Blockchain.calculateHash(transaction));
        }
        return buildMerkleTree(hashes).get(0);
    }

    private static List<String> buildMerkleTree(List<String> hashes) {
        if (hashes.size() == 1) {
            return hashes;
        }
        List<String> newHashes = new ArrayList<>();
        for (int i = 0; i < hashes.size() - 1; i += 2) {
            String combinedHash = hashPair(hashes.get(i), hashes.get(i + 1));
            newHashes.add(combinedHash);
        }
        // If the number of hashes is odd, duplicate the last one
        if (hashes.size() % 2 == 1) {
            newHashes.add(hashes.get(hashes.size() - 1));
        }
        return buildMerkleTree(newHashes);
    }

    private static String hashPair(String hash1, String hash2) {
        return Blockchain.calculateHash(hash1 + hash2);
    }

}
