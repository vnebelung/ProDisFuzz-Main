/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable;

import model.ProtocolFile;
import model.RunnableThread.RunnableState;
import model.runnable.component.LearnCleanC;
import model.runnable.component.LearnDiceC;
import model.runnable.component.LearnHirschbergC;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class LearnR implements the runnable which is responsible for learning
 * the protocol.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LearnR extends AbstractR { // NOPMD

    /**
     * The temporary sequences.
     */
    private final List<List<Byte>> sequences;

    /**
     * The file set.
     */
    private final List<ProtocolFile> files;

    /**
     * The generated sequence which contains fixed and variable parts.
     */
    private List<Byte> sequence;

    /**
     * The learn hirschberg component.
     */
    final private LearnHirschbergC learnHirschbergC;

    /**
     * The learn dice component.
     */
    final private LearnDiceC learnDiceC;

    /**
     * The learn clean component.
     */
    final private LearnCleanC learnCleanC;

    /**
     * Instantiates a new learn runnable.
     *
     * @param files the files which contain the protocol captures
     */
    public LearnR(final List<ProtocolFile> files) {
        super();
        this.files = files;
        sequence = new ArrayList<Byte>();
        learnHirschbergC = new LearnHirschbergC(this);
        learnDiceC = new LearnDiceC(this);
        learnCleanC = new LearnCleanC(this);
        sequences = new ArrayList<List<Byte>>(files.size());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() { // NOPMD
        // Initiates the list of sequences
        List<Byte> bytes;
        for (ProtocolFile file : files) {
            bytes = new ArrayList<Byte>(file.getContent().length); // NOPMD
            for (byte b : file.getContent()) {
                bytes.add(b);
            }
            sequences.add(bytes);
        }
        // Fill the distance matrix
        double[][] distanceMatrix = null;
        double[] avgDistances = null;
        int[] minIndices = new int[2];
        while (sequences.size() > 1 && !isInterrupted()) {
            if (sequences.size() > 2) {
                distanceMatrix = getDistanceMatrix(sequences);
                // Calculate the average distances
                avgDistances = getAverageDistances(distanceMatrix,
                        sequences.size());
                // Calculate new distances
                distanceMatrix = getAvgDistanceMatrix(distanceMatrix,
                        avgDistances);
                // Find minimum distance
                minIndices = getMinimumIndices(distanceMatrix);
            } else {
                minIndices[0] = 0;
                minIndices[1] = 1;
            }
            if (!isInterrupted()) {
                // Execute the Hirschberg algorithm
                sequence = learnHirschbergC.learn(sequences.get(minIndices[0]),
                        sequences.get(minIndices[1]));
            }
            if (!isInterrupted()) {
                // Remove the two learned sequences from the list
                // Remove first minIndices[1] because it is the higher index
                sequences.remove(minIndices[1]);
                setLearned(minIndices[1]);
                sequences.remove(minIndices[0]);
                setLearned(minIndices[0]);
                sequences.add(new ArrayList<Byte>(sequence)); // NOPMD
                if (sequences.size() > 1) {
                    spreadUpdate(RunnableState.RUNNING);
                    sleep(SLEEPING_TIME);
                }
            }
        }
        if (!isInterrupted()) {
            sequences.remove(0);
            spreadUpdate(RunnableState.RUNNING);
        }
        if (!isInterrupted()) {
            sequence = learnCleanC.clean(sequence);
        }
        spreadUpdate(isInterrupted() ? RunnableState.CANCELED
                : RunnableState.FINISHED);
    }

    /**
     * Sets the learned status flag for the not yet learned file with the given
     * index.
     *
     * @param index the index of the not learned file
     */
    private void setLearned(final int index) {
        int count = 0;
        for (ProtocolFile file : files) {
            if (!file.isLearned()) {
                if (count == index) {
                    file.setLearnStatus(true);
                    break;
                } else {
                    count++;
                }
            }
        }
    }

    /**
     * Gets the two indices of the sequences that represent the two sequences
     * with the lowest distance value.
     *
     * @param distanceMatrix the distance matrix
     * @return the two sequence indices with the lowest distance between them
     */
    private int[] getMinimumIndices(final double[][] distanceMatrix) {
        double minDistance = Double.MAX_VALUE;
        int[] indices = new int[2];
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                if (distanceMatrix[i][j] < minDistance) {
                    minDistance = distanceMatrix[i][j];
                    indices[0] = Math.min(i, j);
                    indices[1] = Math.max(i, j);
                }
            }
        }
        return indices;
    }

    /**
     * Gets the distance matrix with temporary calculated distance values based
     * on the average distances of the sequences.
     *
     * @param distanceMatrix the current distance matrix
     * @param avgDistances   the average distances for each sequence
     * @return the new calculated distance matrix
     */
    private double[][] getAvgDistanceMatrix(final double[][] distanceMatrix,
                                            final double[] avgDistances) {
        double[][] matrix = distanceMatrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix[i].length; j++) {
                if (i == j) {
                    matrix[i][j] = Double.MAX_VALUE;
                } else {
                    matrix[i][j] -= (avgDistances[i] + avgDistances[j]);
                    matrix[j][i] = matrix[i][j];
                }
            }
        }
        return matrix;
    }

    /**
     * Gets the average distances for all not yet learned sequences.
     *
     * @param distanceMatrix the distance matrix with all distances
     * @param size           the number of all not yet learned sequences
     * @return the average distances of the sequences
     */
    private double[] getAverageDistances(final double[][] distanceMatrix,
                                         final int size) {
        double[] distances = new double[size];
        double sum;
        for (int i = 0; i < distances.length; i++) {
            sum = 0;
            for (int j = 0; j < distances.length; j++) {
                sum += distanceMatrix[i][j];
            }
            distances[i] = sum / (distances.length - 2);
        }
        return distances;
    }

    /**
     * Gets the distance matrix based on the list of all not yet learned
     * sequences.
     *
     * @param sequences the sequences to calculate the distances of
     * @return the resulting distance matrix
     */
    private double[][] getDistanceMatrix(final List<List<Byte>> sequences) {
        double[][] matrix = new double[sequences.size()][sequences.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix[i].length; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    matrix[i][j] = learnDiceC.getDiceValue(sequences.get(i),
                            sequences.get(j));
                    matrix[j][i] = matrix[i][j];
                }
            }
        }
        return matrix;
    }

    /**
     * Gets the current file list.
     *
     * @return the current file list
     */
    public List<ProtocolFile> getFiles() {
        return files;
    }

    /**
     * Gets the current sequence.
     *
     * @return the sequence
     */
    public List<Byte> getSequence() {
        return sequence;
    }

    /**
     * Gets the number of temporary sequences currently in use by the runnable.
     *
     * @return the number of temporary sequences.
     */
    public int getNumOfTmpSequences() {
        return sequences.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.AbstractRunnable#setTotalProgress()
     */
    @Override
    protected void setTotalProgress() {
        totalProgress = (files.size() - 1)
                * learnHirschbergC.getTotalProgress();
        totalProgress += ((Math.pow(files.size(), 3) - files.size()) / 6 - 1)
                * learnDiceC.getTotalProgress();
        totalProgress += learnCleanC.getTotalProgress();
    }

}