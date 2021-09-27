# About
VVeeb Live is a free and open source app that allows users to create and manage their own overlays for streaming or other use. You will not be able to interact with the overlays though touch and as a result, face tracking is implemented to allow you to implement overlays that you can control with your face gestures. This is ideal for overlays that you would like to use for streaming but you will also need to control them without having to interact with the overlay via touch such as when you're streaming your gaming session and need to check chat history. This will allow you to do that wihtout needing to use up your precious screen real-estate for a floating chat box that is uninteractive in terms of the game. and can potentially limit visibility of the game

## Prebuilt examples
There are various examples of overlays that you can use located in the [vveeb-overlay-sample](https://github.com/muggy8/vveeb-overlay-sample) repository. Follow the instructions and setup simple overlays for your streams or use them as examples to develop a more complex overlay.

## About face tracking gestures
The detected face gestures will be passed to the web page via [postMessage](https://developer.mozilla.org/en-US/docs/Web/API/Window/postMessage). Work In Progress
