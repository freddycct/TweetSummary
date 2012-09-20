clear;
close all;

init_names;

lda_pr       = load('../results/facebook_ipo/K=8/lda/prec_recall_3.txt');
%np_lda_pr    = load('../results/facebook_ipo/K=8/np_lda/prec_recall_3.txt');
%decay_lda_pr = load('../results/facebook_ipo/K=8/decay_lda/prec_recall_3.txt');
gauss_lda_pr = load('../results/facebook_ipo/K=8/gauss_lda/prec_recall_3.txt');

cmap = distinguishable_colors(2);

figure;hold on;
plot(lda_pr(2:end, 3), lda_pr(2:end, 2), '.', 'Color', cmap(1,:));
%plot(np_lda_pr(2:end, 3), np_lda_pr(2:end, 2),    'o', 'Color', cmap(2,:));
%plot(decay_lda_pr(2:end, 3), decay_lda_pr(2:end, 2), 'x', 'Color', cmap(3,:));
plot(gauss_lda_pr(2:end, 3), gauss_lda_pr(2:end, 2), '*', 'Color', cmap(2,:));
hold off;

xlabel('Recall', 'FontSize', 16);
ylabel('Precision', 'FontSize', 16);
title('PR Curve of Search Results (Facebook IPO)');
legend(model_names, 'FontSize', 12);
saveas(gcf, '../latex/eps/facebook_ipo/pr_curves.eps', 'psc2');