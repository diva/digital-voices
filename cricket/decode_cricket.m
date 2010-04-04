% Copyright 2001 by the authors. All rights reserved.
% Authors: Cristina V Lopes (crista at tagide dot com)
%          Patricio de la Guardia
%
% Permission is hereby granted, free of charge, to any person obtaining a copy
% of this software and associated documentation files (the "Software"), to deal
% in the Software without restriction, including without limitation the rights
% to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
% copies of the Software, and to permit persons to whom the Software is
% furnished to do so, subject to the following conditions:
%
% The above copyright notice and this permission notice shall be included in
% all copies or substantial portions of the Software.
%
% THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
% IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
% FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
% AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
% LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
% OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
% THE SOFTWARE.

clear all
close all
[signal, fs, nbits] = wavread('200cm_2straight_2reverse.wav');

shortsize = 15;
longsize = 24;
Freq = 4184;
framelength = 420;
packetlength = shortsize;
silencelength = longsize;
silencepacketlength = shortsize + longsize;
silencepacketsamples = round(fs*(shortsize + longsize)/1000);
threepacketsamples = round(fs*3*silencepacketlength/1000);
framesamples = round(fs*framelength/1000);

output=[];
threshwidth = 9;
threshamplitude = 0.75;

%here we need to synchronize
window = hanning(0.1*fs);
time = 0:1/fs:(fs*0.1-1)/fs;
hail = exp(sqrt(-1)*2*pi*time*Freq);
hail = hail.*window';
startindex = synchronize(signal(1:fs*5), hail');
maxvalue=max(abs(signal(startindex:startindex+length(hail))));
startindex=startindex+length(hail)
% normalize
signal=signal/maxvalue;

winner = 0;
for i = startindex:framesamples:length(signal)
    eot=detect(signal(i:i+length(hail)),Freq,fs)
    if (eot > 0.15)
        break;
    end
    maxpower = 0;
    %First symbol
    for k = 1:4
        
        beggining = i + round(fs*(k-1)*(2*packetlength + 1.5*silencelength)/1000);
        if (beggining + threepacketsamples > length(signal))
            signal = [signal; zeros(length(signal)-beggining + threepacketsamples+1, 1)];
        end 
        power = norm(signal(beggining:beggining + threepacketsamples));
        %figure(2), plot(signal(beggining:beggining + threepacketsamples)),pause
        if power > maxpower
             maxpower = power;
            winner = k;
        end
    end
    %Second Symbol, we need the possition of the three packets
    begg = i + round(fs*(winner-1)*(2*packetlength + 1.5*silencelength)/1000);
    for jj = 1:3
        currframe = signal(begg + silencepacketsamples*(jj-1):begg + silencepacketsamples*jj);
        %figure(2), plot(currframe),pause
        amplitude(jj) = abs(max(currframe));
        width(jj) = norm(currframe/amplitude(jj));
        if width(jj) < threshwidth
            if amplitude(jj) < threshamplitude
                value(jj) = 2;
            else 
                value(jj) = 1;
            end
        else %width(jj) >= threshwidth
            if amplitude(jj) < threshamplitude
                value(jj) = 4;
            else 
                value(jj) = 3;
            end
        end
    end
%     plot(signal(begg :begg + silencepacketsamples*3))
%     winner
%     value 
%     pause;    
    output = [output, winner, value];
end
output
text = huffdecode(output)
