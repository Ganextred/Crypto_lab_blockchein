import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Blockchain {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final List<Block> chain;
    private final List<Transaction> currentTransactions;

    public Blockchain() {
        chain = new ArrayList<>();
        currentTransactions = new ArrayList<>();

        // Create the genesis block

        Block genesisBlock = new Block(new ArrayList<>());
        genesisBlock.getHead().setTimestamp(System.currentTimeMillis());
        genesisBlock.getHead().setPreviousHash(null); // Previous hash of the genesis block is null
        genesisBlock.getHead().setIndex(0);
        genesisBlock.getHead().setNonce(0);
        chain.add(genesisBlock);
    }


    // Constructor, getters

    public void addTransaction(Transaction transaction) {
        currentTransactions.add(transaction);
    }

    private void addBlock(Block block){
        //TODO: validate head
        chain.add(block);

    }

    public Block mine() {

        var lastBlock = chain.get(chain.size()-1);
        var lastBlockHead = lastBlock.getHead();
        var headHash = calculateHash(lastBlockHead);

        Block block = new Block(currentTransactions);
        currentTransactions.clear();

        block.getHead().setTimestamp(System.currentTimeMillis());
        block.getHead().setPreviousHash(headHash);
        block.getHead().setIndex(lastBlock.getHead().getIndex());
        block.getHead().setNonce(0);

        // Proof-of-work
        while (!isValidProof(block)) {
            block.getHead().setNonce(block.getHead().getNonce() + 1);
        }
        addBlock(block);

        return block;
    }

    public boolean isValidProof(Block block) {
        String hash = calculateHash(block);
        return hash.startsWith("00");
    }

    public boolean isValidChain() {
        Block previousBlock = chain.get(0);
        int currentIndex = 1;

        while (currentIndex < chain.size()) {
            Block currentBlock = chain.get(currentIndex);

            // Check if the previousHash of the current block matches the hash of the previous block
            if (!currentBlock.getHead().getPreviousHash().equals(calculateHash(previousBlock.getHead()))) {
                return false;
            }

            // Check if the current block's hash meets the proof-of-work criteria
            if (!isValidProof(currentBlock)) {
                return false;
            }

            // Check if the Merkle root hash of transactions in the current block is correct
            String calculatedMerkleRootHash = Block.calculateMerkleRootHash(currentBlock.getTransactions());
            if (!calculatedMerkleRootHash.equals(currentBlock.getHead().getMerkleRootHash())) {
                return false;
            }

            // Move to the next block
            previousBlock = currentBlock;
            currentIndex++;
        }

        return true;
    }

    public void displayFundsForBlock(int blockIndex) {
        Block specifiedBlock = chain.get(blockIndex);

        if (specifiedBlock == null) {
            System.out.println("Block not found.");
            return;
        }

        Map<String, Double> accountBalances = new HashMap<>();

        // Iterate through all transactions in the chain up to the specified block
        for (Block b : chain) {
            if (b.getHead().getIndex() > blockIndex) {
                break; // Stop if we've reached the specified block
            }

            // Update account balances based on transactions in the block
            for (Transaction transaction : b.getTransactions()) {
                accountBalances.put(transaction.getSender(), accountBalances.getOrDefault(transaction.getSender(), 0.0) - transaction.getAmount());
                accountBalances.put(transaction.getRecipient(), accountBalances.getOrDefault(transaction.getRecipient(), 0.0) + transaction.getAmount());
            }
        }

        // Display all those who have funds
        System.out.println("Accounts with funds in block " + blockIndex + ":");
        for (Map.Entry<String, Double> entry : accountBalances.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Calculate min/max amount of funds on each person's account for the entire previous history
        double minBalance = Double.MAX_VALUE;
        double maxBalance = Double.MIN_VALUE;
        String minAccount = null;
        String maxAccount = null;
        for (Map.Entry<String, Double> entry : accountBalances.entrySet()) {
            if (entry.getValue() < minBalance) {
                minBalance = entry.getValue();
                minAccount = entry.getKey();
            }
            if (entry.getValue() > maxBalance) {
                maxBalance = entry.getValue();
                maxAccount = entry.getKey();
            }
        }

        System.out.println("Minimum balance: " + minBalance + " (Account: " + minAccount + ")");
        System.out.println("Maximum balance: " + maxBalance + " (Account: " + maxAccount + ")");
    }

    @SneakyThrows
    public static String calculateHash(Object object) {
        var serialisedObject = OBJECT_MAPPER.writeValueAsString(object).getBytes(StandardCharsets.UTF_8);
        return DigestUtils.sha256Hex(serialisedObject);
    }


}
