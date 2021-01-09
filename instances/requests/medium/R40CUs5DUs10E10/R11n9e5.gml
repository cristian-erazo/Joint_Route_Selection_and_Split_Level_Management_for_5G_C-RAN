graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
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
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 2
    prc 4
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 3
    prb 5
    x 64
    y 55
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 3
    prb 4
    x 83
    y 78
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 6
    prb 6
    x 19
    y 88
  ]
  node [
    id 7
    label "7"
    type 1
    prc 5
    ant 9
    prb 5
    x 48
    y 18
  ]
  node [
    id 8
    label "8"
    type 1
    prc 5
    ant 10
    prb 5
    x 19
    y 106
  ]
  edge [
    source 0
    target 8
    bandwith 575
    delay 457
  ]
  edge [
    source 1
    target 6
    bandwith 812
    delay 104
  ]
  edge [
    source 2
    target 4
    bandwith 740
    delay 301
  ]
  edge [
    source 3
    target 5
    bandwith 266
    delay 185
  ]
  edge [
    source 3
    target 7
    bandwith 895
    delay 240
  ]
]
