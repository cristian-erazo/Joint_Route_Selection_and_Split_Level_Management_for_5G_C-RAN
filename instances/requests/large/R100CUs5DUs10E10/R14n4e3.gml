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
    type 1
    prc 4
    ant 7
    prb 3
    x 50
    y 114
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 3
    prb 2
    x 100
    y 113
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 9
    prb 6
    x 19
    y 29
  ]
  edge [
    source 0
    target 1
    bandwith 560
    delay 397
  ]
  edge [
    source 0
    target 2
    bandwith 226
    delay 414
  ]
  edge [
    source 0
    target 3
    bandwith 820
    delay 155
  ]
]
