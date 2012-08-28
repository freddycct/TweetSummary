clear;
%close all;
events = {'facebook_ipo', 's3'};
event_names = {'Facebook IPO', 'Samsung Galaxy SIII'};
num_folds = [5, 10];

%for e=1:length(events)
for e=1:1
    if e==1
        min_topic = 6;
        max_topic = 11;
    elseif e==2
        min_topic = 10;
        max_topic = 15;
    end
    %calculate number of topics
    num_topics = max_topic - min_topic + 1;
    colors = lines(num_topics);
    legend_str = cell(num_topics, 1);
    for k=min_topic:max_topic
        legend_str(k - min_topic + 1) = { sprintf('%d topics', k) };
    end
    
    for nf=1:length(num_folds)
        %# of Topics <tab> Decay <tab> Maximum Likelihood <tab> Perplexity
        perplexity = load(sprintf('../results/%s/%dfolds_perplexity.txt', events{e}, num_folds(nf)));
       
        %figure;
        %delta_perplexity = perplexity(:, 2) == -1e-6;
        %plot(perplexity(delta_perplexity, 1), perplexity(delta_perplexity, 3), '.');
        
        %{
        figure('visible', 'off');hold on;
        for k=min_topic:max_topic
            k_perplexity = perplexity(:,1) == k;
            plot(perplexity(k_perplexity, 3), perplexity(k_perplexity, 4), '.-', 'Color', colors(k - min_topic + 1, :));
        end
        
        xlabel('Likelihood', 'FontSize', 16);
        ylabel('Perplexity', 'FontSize', 16);
        title(sprintf('%s with %d Number of Cross Validation', event_names{e}, num_folds(e)), 'FontSize', 16);
        legend(legend_str, 'FontSize', 16, 'Location', 'NorthEast');
        hold off;
        saveas(gcf, sprintf('../latex/eps/perplexity_likelihood_%s_%dfolds.eps', events{e}, num_folds(nf)), 'psc2');
        close(gcf);
        %}
        
        figure('visible', 'off');hold on;
        for k=min_topic:max_topic
            k_perplexity = perplexity(:,1) == k;
            plot( log10(-perplexity(k_perplexity, 2)), perplexity(k_perplexity, 4), '*-', 'Color', colors(k - min_topic + 1, :) );
        end
        
        xlabel('log_{10}(\delta)', 'FontSize', 16);
        ylabel('Perplexity', 'FontSize', 16);
        title(sprintf('%s with %d Number of Cross Validation', event_names{e}, num_folds(nf)), 'FontSize', 16);
        legend(legend_str, 'FontSize', 16, 'Location', 'NorthEast');
        hold off;
        saveas(gcf, sprintf('../latex/eps/perplexity_delta_%s_%dfolds.eps', events{e}, num_folds(nf)), 'psc2');
        close(gcf);
        
        figure('visible', 'off');hold on;
        for k=min_topic:max_topic
            k_perplexity = perplexity(:,1) == k;
            plot( log10(-perplexity(k_perplexity, 2)), perplexity(k_perplexity, 3), 'o-', 'Color', colors(k - min_topic + 1, :) );
        end
        
        xlabel('log_{10}(\delta)', 'FontSize', 16);
        ylabel('Likelihood', 'FontSize', 16);
        title(sprintf('%s with %d Number of Cross Validation', event_names{e}, num_folds(nf)), 'FontSize', 16);
        legend(legend_str, 'FontSize', 16, 'Location', 'SouthEast');
        hold off;
        saveas(gcf, sprintf('../latex/eps/delta_likelihood_%s_%dfolds.eps', events{e}, num_folds(nf)), 'psc2');
        close(gcf);
    end
end
