close all; clear;

lda_pr       = load('../results/facebook_ipo/K=8/lda/prec_recall_3.txt');
np_lda_pr    = load('../results/facebook_ipo/K=8/np_lda/prec_recall_3.txt');
decay_lda_pr = load('../results/facebook_ipo/K=8/decay_lda/prec_recall_3.txt');
gauss_lda_pr = load('../results/facebook_ipo/K=8/gauss_lda/prec_recall_3.txt');

cmap = jet(4);

figure;hold on;
plot(lda_pr(:, 2),       lda_pr(:, 1),       '.', 'Color', cmap(1,:));
plot(np_lda_pr(:, 2),    np_lda_pr(:, 1),    '.', 'Color', cmap(2,:));
plot(decay_lda_pr(:, 2), decay_lda_pr(:, 1), '.', 'Color', cmap(3,:));
plot(gauss_lda_pr(:, 2), gauss_lda_pr(:, 1), '.', 'Color', cmap(4,:));
hold off;