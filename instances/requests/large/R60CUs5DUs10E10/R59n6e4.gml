graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 2
    prb 3
    x 58
    y 65
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 4
    prb 5
    x 112
    y 23
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 8
    prb 2
    x 68
    y 29
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 4
    prb 4
    x 49
    y 43
  ]
  edge [
    source 0
    target 4
    bandwith 655
    delay 315
  ]
  edge [
    source 0
    target 3
    bandwith 535
    delay 317
  ]
  edge [
    source 1
    target 2
    bandwith 628
    delay 390
  ]
  edge [
    source 1
    target 5
    bandwith 282
    delay 160
  ]
]
