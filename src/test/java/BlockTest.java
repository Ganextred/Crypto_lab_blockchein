import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockTest {

    @Test
    public void testMerkleTree() {
        // Create a list of transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("Alice", "Bob", 10.0));
        transactions.add(new Transaction("Bob", "Charlie", 5.0));
        transactions.add(new Transaction("Charlie", "Dave", 3.0));
        List<String> hashes = transactions.stream().map(Blockchain::calculateHash).toList();

        // Calculate the expected Merkle root hash manually
        String expectedMerkleRootHash = Block.calculateMerkleRootHash(transactions);

        // Calculate the Merkle root hash using the method in the Blockchain class
        String actualMerkleRootHash = Blockchain.calculateHash(
                Blockchain.calculateHash(hashes.get(0)+hashes.get(1))+
                    Blockchain.calculateHash(hashes.get(2)+hashes.get(2)));

        // Verify that the calculated Merkle root hash matches the expected value
        assertEquals(expectedMerkleRootHash, actualMerkleRootHash);
    }

}
