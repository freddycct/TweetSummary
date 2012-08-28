clear;
close all;

%data   = {'sfbay', 'sfbay', 'gardenhose', 'gardenhose'};

models = {'lda', 'np_lda', 'decay_lda', 'gauss_lda'};
events = {'facebook_ipo', 'obamacare', 'japan', 'bp', 'wallstreet'};
event_names = {'Facebook IPO', 'Samsung Galaxy SIII', 'Obamacare', 'Japan Earthquake'};

e = 1;
m = 4;
K = 8;

%for e=1:length(events)
%for m=1:length(models)
    %Load the files
    distribution = load( sprintf('../results/%s/K=%d/%s/distribution.txt', events{e}, K, models{m} ));
    time = load( sprintf('../results/%s/K=%d/%s/time.txt', events{e}, K, models{m} ));
    %originals = load( sprintf('../results/%s/%s/originals.txt', events{e}, models{m} ));
    %retweets = load( sprintf('../results/%s/%s/retweets.txt', events{e}, models{m} ));
    %End of loading files
    
    %make the directory
    mkdir(sprintf('../latex/eps/%s/%s/', events{e}, models{m}));
    %end creating
    
    %Get the Number of Tweets & Number of Topics
    [T, K] = size(distribution);
    %End
    
    %Initialize the dates
    init_dates;
    %End of dates
    
    %Form the legend string array
    legend_str = cell(K, 1);
    for k=1:K
        legend_str(k) = {sprintf('Topic %d', k)};
    end
    %End of legend
    
    %For possible aesthetics purposes
    topic_order = 1:K;
    %End
    
    %Get the Histogram
    begin_time = time(1);
    end_time   = time(end);
    time_series = begin_time:day:end_time;
    time_series(end) = end_time;
    
    time_bins = 0.5 .* (time_series(2:end) + time_series(1:end-1));
    time_bins(end+1) = time_bins(end) + day;
    
    time_frequency = histc(time, time_series);
    %End of Histogram
    
    %Plot the Histogram
    bar(time_bins, time_frequency); hold on;
    xlabel('Tiime (ms)', 'FontSize', 16);
    ylabel('# of Tweets', 'FontSize', 16);
    title(event_names{e}, 'FontSize', 16);
    %End plot Histogram
    
    %Plot the time labels of the histogram
    [~, index] = max(time_frequency);
    date = get_date(time_bins(index));
    ylimits = ylim;
    plot([time_bins(index) time_bins(index)], ylim, '--');
    text(time_series(index) + day, ylimits(2) * 0.95, date, 'FontSize', 16);
    hold off;
    saveas(gcf, sprintf('../latex/eps/%s/%s/tweet_frequency.eps', events{e}, models{m}), 'psc2');
    close(gcf);
    %End plot time labels
    
    %Get the Histogram for Topic Distribution
    ep_topics = zeros(length(time_series), K);
    for i=1:length(time_series)-1
        selection = (time_series(i) <= time) & (time < time_series(i+1));
        ep_topics(i, :) = sum(distribution(selection, :), 1);
    end
    selection = time >= time_series(end);
    ep_topics(end, :) = ep_topics(end, :) + sum(distribution(selection, :), 1);
    
    for k=1:K
        bar(time_bins, ep_topics(:, k));hold on;
        xlabel('Time (ms)', 'FontSize', 16);
        ylabel('Expected Value', 'FontSize', 16);
        title(legend_str(k), 'FontSize', 16);
        
        [~, index] = max(ep_topics(:, k));
        date = get_date(time_bins(index));
        ylimits = ylim;
        plot([time_bins(index) time_bins(index)], ylim, '--');
        text(time_bins(index) + day, ylimits(2) * 0.95, date, 'FontSize', 16);
        hold off;
        saveas(gcf, sprintf('../latex/eps/%s/%s/topic_%d.eps', events{e}, models{m}, k), 'psc2');
        close(gcf);
    end
    %End of this Topical Histogram
    
    %Combine all the Topic Histogram into single plot
    bar(time_bins, ep_topics, 1.5);hold on;
    xlabel('Tiime (ms)', 'FontSize', 16);
    ylabel('Expected Value', 'FontSize', 16);
    title(event_names{e}, 'FontSize', 16);
    legend(legend_str, 'Location', 'NorthEast');
    
    [~, index1] = max(ep_topics);
    [~, index] = max(max(ep_topics));
    date = get_date(time_bins(index1(index)));
    
    plot([time_bins(index1(index)) time_bins(index1(index))], ylim, '--');
    text(time_bins(index1(index)) + day, ylimits(2) * 0.95, date, 'FontSize', 16);
    hold off;
    saveas(gcf, sprintf('../latex/eps/%s/%s/topics.eps', events{e}, models{m}), 'psc2');
    close(gcf);
    %End
    
    %Plot normalized Topic Histogram
    ep_topics_normalized = zeros(size(ep_topics));
    for k=1:K
        ep_topics_normalized(:, k) = ep_topics(:, k) ./ (time_frequency + eps);
        bar(time_bins, ep_topics_normalized(:, k)); hold on;
        xlabel('Time (ms)', 'FontSize', 16);
        ylabel('Normalized Expected Value', 'FontSize', 16);
        title(legend_str(k), 'FontSize', 16);
        
        [~, index] = max(ep_topics_normalized(:, k));
        date = get_date(time_bins(index));
        ylimits = ylim;
        plot([time_bins(index) time_bins(index)], ylim, '--');
        text(time_bins(index) + day, ylimits(2) * 0.95, date, 'FontSize', 16);
        hold off;
        saveas(gcf, sprintf('../latex/eps/%s/%s/topic_normalized_%d.eps', events{e}, models{m}, k), 'psc2');
    end
    %End of Plot
    
    %Combine all the normalized Topic Histogram into single plot
    bar(time_bins, ep_topics_normalized, 1.5);hold on;
    xlabel('Tiime (ms)', 'FontSize', 16);
    ylabel('Normalized Expected Value', 'FontSize', 16);
    title(event_names{e}, 'FontSize', 16);
    legend(legend_str, 'Location', 'NorthEast');
    ylimits = ylim;
    
    [~, index1] = max(ep_topics_normalized);
    [~, index] = max(max(ep_topics_normalized));
    date = get_date(time_bins(index1(index)));
    
    plot([time_bins(index1(index)) time_bins(index1(index))], ylim, '--');
    text(time_bins(index1(index)) + day, ylimits(2) * 0.95, date, 'FontSize', 16);
    hold off;
    saveas(gcf, sprintf('../latex/eps/%s/%s/topics_normalized.eps', events{e}, models{m}), 'psc2');
    close(gcf)
    %End of Plot
    
    %Use time_series
    if m==4
        gaussian_topics = load( sprintf('../results/%s/K=%d/%s/gaussian_topics.txt', events{e}, K, models{m} ));
        cmap = jet(K);
        figure;hold on;
        for k=1:K
            p_t = (1/sqrt(2 * pi * gaussian_topics(k, 3))) .* exp(-((time_series - gaussian_topics(k, 2)).^2)./(2 * gaussian_topics(k, 3)));
            plot(time_series, p_t, 'Color', cmap(k,:), 'LineWidth', 2);
        end
        legend(legend_str, 'Location', 'NorthEast');
        hold off;
        saveas(gcf, sprintf('../latex/eps/%s/%s/gaussian_topics.eps', events{e}, models{m}), 'psc2');
        close(gcf);
    end
    %End of time series
%end

%{
time_freq_originals = histc(time(originals), time_series);
time_freq_retweets  = histc(time(retweets),  time_series);

distribution_originals = distribution(originals, :);
distribution_retweets  = distribution(retweets, :);

%Now plot the histogram of originals
ep_topics_originals = zeros(size(ep_topics));
for i=1:length(time_series)-1
    selection = (time_series(i) <= time(originals)) & (time(originals) < time_series(i+1));
    ep_topics_originals(i, :) = sum(distribution_originals(selection, :), 1);
end
selection = time(originals) >= time_series(end);
ep_topics_originals(end, :) = ep_topics_originals(end, :) + sum(distribution_originals(selection, :), 1);
%End plot of histogram

%Now plot the histogram of retweets
ep_topics_retweets = zeros(size(ep_topics));
for i=1:length(time_series)-1
    selection = (time_series(i) <= time(retweets)) & (time(retweets) < time_series(i+1));
    ep_topics_retweets(i, :) = sum(distribution_retweets(selection, :), 1);
end
selection = time(retweets) >= time_series(end);
ep_topics_retweets(end, :) = ep_topics_retweets(end, :) + sum(distribution_retweets(selection, :), 1);
%End plot of histogram

for k=1:K
    figure;
    subplot(2,1,1), bar(time_bins, ep_topics_originals(:, k));
    subplot(2,1,2), bar(time_bins, ep_topics_retweets(:, k));
end

%what kind of topics, will attract a larger proportion of retweets than originals
ep_topics_normalized_originals = ep_topics_originals ./ (ep_topics + eps);
ep_topics_normalized_retweets = ep_topics_retweets ./ (ep_topics + eps);

for k=1:K
    figure
    subplot(2,1,1), bar(time_bins, ep_topics_normalized_originals(:, k));
    subplot(2,1,2), bar(time_bins, ep_topics_normalized_retweets(:, k));
end
%}
