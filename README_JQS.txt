                           jQuizShow
               Copyright(c) 2001 Steven D. Chen


Licensing:
==========

    This file is part of jQuizShow.

    jQuizShow is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    jQuizShow is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License (GPL)
    along with jQuizShow; if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


Requirements:
=============

    In order to run the jQuizShow, you *must* have the Java2 SE Runtime
    Environment (JRE) installed on your system.  jQuizShow requires
    JRE 1.3.1 or later.  JRE 1.3.1 is available for free download from
    Sun Microsystems, Inc.'s Java 2 site:

        http://java.sun.com/j2se/1.3/download.html

    NOTE:  Download the "JRE" version unless you plan on doing Java
	   code development.

    If your operating system is not listed on the primary download site,
    you may want to try the Java Platform Ports web page:

        http://java.sun.com/cgi-bin/java-ports.cgi


Starting jQuizShow
==================

    Microsoft Windows 98/NT/2000/ME/XP
    ==================================

	If you have the Java 1.3.1 Runtime Environment or later installed,
	simply double click on the jQuizShow.jar file to start it.

        If, for some reason this does not work, try double clicking on (or
        running from a Command Prompt window) the "jQuizShow.bat" file.

	Optionally, the jQuizShow can be run manually from the Command Prompt
        window.  To do so, follow the instructions under "Other Operating
        Systems" below.


    MacOS X
    =======

        An appropriate JRE is released as part of MacOS X.  To run
	jQuizShow, simply double-click on the jQuizShow.jar icon to
	start it.


    Other Operating Systems
    =======================

	From the shell window, change your current directory to the directory
	containing the file:

		jQuizShow.jar

	Enter the following command:

		java -jar jQuizShow.jar

	to start the jQuizShow program.


    Troubleshooting
    ===============

        Symptom:

                "java" is not found or is an invalid command.

            Solution(s):

               1) Ensure that the Java 1.3.1 Runtime Environment (or later)
	          is properly installed.

               2) Ensure that the "java" program is in your PATH
                  environment.

            
Playing the game:
=================

    jQuizShow was designed to work in a group environment, with a "hot
    seat", game host, audience and simulated "call-a-friend".  Thus, the
    controls are centered around either the game host selecting and
    controlling the game play or a 3rd party.

    Select "New Player..." from the File menu to start a game.

    The primary keys are:

        SPACE BAR         -- Move from one state to another
        ENTER KEY         -- Finalize the answer or use helpline
        letter keys       -- Select an answer

        , (comma)         -- Select 50/50 helpline
        . (period)        -- Select Call-a-Friend helpline
        / (slash)         -- Select Ask-the-Audience helpline
        ENTER KEY           -- End Call-a-Friend helpline mode

        BACKSPACE KEY     -- Cancels helpline selection or finalized
	                     answer

        = (equals)        -- Walk away from game with current earnings

        ~ (tilde)         -- Toggle score screen on/off

    The SPACE BAR is the most used key.  If in doubt what to do, press it to
    move to the next game state (e.g. to display each possible answer
    one-by-one, to move from the transition screen to the next round,
    start/stop the Call-a-Friend timer, etc.).

    In addition, the number keys (0-9) can be configured to play custom
    sound samples.

    NOTE:  If for some reason the keys do not appear to work, try using
    the mouse to select the "Game Screen" to make it the active window
    (highlighted border).


Architecture:
=============

    The jQuizShow is based on the popular "Who Wants To Be A Millionaire"
    (WWTBAM) TV game show.  It is targeted for use in a group environment,
    such as a church youth group, Boy/Girl Scouts, schools or informal
    meetings.  The original design was for the game to be projected on to a
    screen using a computer projector.  An optional TV input card for the
    computer or "web cam" can be used to overlay live video images from
    in the center of the game screen.  The camera could focus on the
    contestant in the "hot seat" to truly simulate the WWTBAM experience.


Other notes:
============

    * The questions included with the jQuizShow are geared toward the Jr.
      High and Sr. High school age group.

    * Only very basic sound effects are built into the jQuizShow.  However,
      there is a sound library "jQuizShowSounds.jar" that has been made
      available as a separate download that provides additional sounds
      and background music for the game.  This library was released as
      an add-on to the jQuizShow for copyright reasons.

      For more information and instructions for downloading this library,
      please visit the jQuizShow website:

          http://quizshow.sourceforge.net


Contact:
========

    Home page:

        http://quizshow.sourceforge.net

    Bug reports/suggestions/development/source:

        http://sourceforge.net/projects/quizshow


