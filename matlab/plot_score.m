clear;
close all;

init_names;

K = 5:10;

%load the MTurk Results

mturk = [
    28 49 28 55;
    17 33 55 53;
	48 29 46 32;
    37 33 17 67;
    34 62 32 28
    ]

mturk(:,2:3) = [];
bar(mturk);
set(gca, 'XTickLabel', event_names);
title('Amazon Mechanical Turk Votes');
saveas(gcf, '../latex/eps/mturk.eps', 'psc2');

for e=1:length(events)
    
    scores = zeros(length(models), length(K));
    
    for k=1:length(K)
        scores(:, k) = load(sprintf('../results/%s/K=%d/coherent_bleu.txt', events{e}, K(k)));
    end
    
    scores(2:3,:) = []; %temporary, remove later
    
    figure;
    bar(K, scores');
    xlabel('# of Topics', 'FontSize', 16);
    ylabel('Ngram Score', 'FontSize', 16);
    title(event_names{e}, 'FontSize', 16);
    legend(model_names, 'FontSize', 14, 'Location', 'Best');
    saveas(gcf, sprintf('../latex/eps/%s/score.eps', events{e}), 'psc2');
end
