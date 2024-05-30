import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockchainTest {

    private Blockchain blockchain;

    @BeforeEach
    public void setUp() {
        blockchain = new Blockchain();
    }

    @Test
    public void testBlockchain() {
        // Adding transactions
        blockchain.addTransaction(new Transaction("Alice", "Bob", 10.0));
        //blockchain.addTransaction(new Transaction("Alice", "Bob", 10.0));
        blockchain.addTransaction(new Transaction("Bob", "Charlie", 5.0));
        Block minedBlock = blockchain.mine();
        blockchain.addTransaction(new Transaction("Charlie", "David", 7.0));
        blockchain.addTransaction(new Transaction("David", "Alice", 3.0));
        Block minedBlock2 = blockchain.mine();
        blockchain.addTransaction(new Transaction("Alice", "David", 7.0));
        blockchain.addTransaction(new Transaction("David", "Charlie", 3.0));
        Block minedBlock3 = blockchain.mine();
        blockchain.addTransaction(new Transaction("David", "Alice", 9.0));
        blockchain.addTransaction(new Transaction("David", "Charlie", 3.0));
        blockchain.addTransaction(new Transaction("Charlie", "Charlie", 3.0));
        Block minedBlock4 = blockchain.mine();

        // Verifying proof-of-work
        assertTrue(blockchain.isValidProof(minedBlock));

        // Verifying Merkle root hash calculation
        String calculatedMerkleRootHash = Block.calculateMerkleRootHash(
                blockchain.getChain().get(1).getTransactions());
        assertEquals(calculatedMerkleRootHash, minedBlock.getHead().getMerkleRootHash());

        System.out.println("Bock 1");
        blockchain.displayFundsForBlock(1);
        System.out.println("Bock 2");
        blockchain.displayFundsForBlock(2);
        System.out.println("Bock 4");
        blockchain.displayFundsForBlock(4);

        // Verifying block addition
        assertEquals(5, blockchain.getChain().size());
        assertTrue(blockchain.isValidChain());
    }
}
