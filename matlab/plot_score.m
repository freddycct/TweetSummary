clear;
close all;

models = {'lda', 'np_lda', 'decay_lda', 'gauss_lda'};
model_names = {'LDA', 'NP LDA', 'Decayed LDA', 'Gaussian LDA'};
lines = [ 1, 257, 180, 119, 98 ];
events = {'facebook_ipo', 'obamacare', 'japan', 'bp', 'wallstreet'};
event_names = {'Facebook IPO', 'Obamacare', 'Japan Earthquake', 'BP Oil Spill', 'Occupy Wall Street'};

K = 5:10;
scores = zeros(length(models), length(K));

for e=1:length(events)
    for k=1:length(K)
        scores(:, k) = load(sprintf('../results/%s/K=%d/coherent_bleu.txt', events{e}, K(k)));
    end
    figure;
    bar(K, scores');
    xlabel('# of Topics', 'FontSize', 16);
    ylabel('Ngram Score', 'FontSize', 16);
    title(event_names{e}, 'FontSize', 16);
    legend(model_names, 'FontSize', 14, 'Location', 'Best');
    saveas(gcf, sprintf('../latex/eps/%s/score.eps', events{e}), 'psc2');
end
